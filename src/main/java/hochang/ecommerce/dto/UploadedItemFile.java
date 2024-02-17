package hochang.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UploadedItemFile {
    private String thumbnailUploadFileName;

    private List<String> imageUploadFileNames = new ArrayList<>();

}
