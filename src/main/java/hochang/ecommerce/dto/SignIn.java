package hochang.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class SignIn {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
