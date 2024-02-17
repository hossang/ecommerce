package hochang.ecommerce.util.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3FileStore {
    private static final int TARGET_WIDTH = 450;
    private static final int TARGET_HEIGHT = 450;
    private static final int EXTENSION_POSITION = 1;
    private static final String S3_PATH = "ecommerce/";
    private static final String PUBLIC_MAX_AGE_3600 = "public,max-age=3600";

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String getS3FullPath(String fileName) {
        return S3_PATH + fileName;
    }

    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        BufferedImage bufferedImage = ImageIO.read(multipartFile.getInputStream());
        log.info("multipartFile.getInputStream() : {}", multipartFile.getInputStream());
        bufferedImage = resizeImage(bufferedImage, TARGET_WIDTH, TARGET_HEIGHT);
        Path filePath = Path.of(storeFileName);
        ImageIO.write(bufferedImage, "jpg", Files.newOutputStream(filePath));

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(S3_PATH + storeFileName)
                .acl(ObjectCannedACL.BUCKET_OWNER_FULL_CONTROL)
                .cacheControl(PUBLIC_MAX_AGE_3600)
                .build();

        s3Client.putObject(putObjectRequest, filePath);
        deleteLocalFile(filePath);
        return new UploadFile(originalFilename, storeFileName);
    }

    public void deleteLocalFile(Path path) throws IOException {
        Files.deleteIfExists(path);
    }

    public void deleteS3File(String storeFileName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(S3_PATH + storeFileName)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        return Scalr.resize(originalImage, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_EXACT, targetWidth, targetHeight, Scalr.OP_ANTIALIAS);
    }

    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + EXTENSION_POSITION);
    }
}
