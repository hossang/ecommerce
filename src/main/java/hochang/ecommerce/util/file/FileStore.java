package hochang.ecommerce.util.file;

import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class FileStore {
    private static final int TARGET_WIDTH = 450;
    private static final int TARGET_HEIGHT = 450;

    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }
        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        BufferedImage bufferedImage = ImageIO.read(multipartFile.getInputStream());
        bufferedImage = resizeImage(bufferedImage, TARGET_WIDTH, TARGET_HEIGHT);
        Path filePath = Path.of(getFullPath(storeFileName));

        ImageIO.write(bufferedImage, "jpg", Files.newOutputStream(filePath));
        return new UploadFile(originalFilename, storeFileName);
    }

    public void deleteFile(String filename) throws IOException {
        Path filePath = Paths.get(fileDir).resolve(filename).normalize();
        Files.deleteIfExists(filePath);
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
        return originalFilename.substring(pos + 1);
    }
}
