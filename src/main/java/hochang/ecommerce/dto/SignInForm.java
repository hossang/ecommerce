package hochang.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class SignInForm {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
