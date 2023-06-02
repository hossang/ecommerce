package hochang.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MultipartException.class)
    public String exceedFileSize(RedirectAttributes redirectAttributes) {
        String errorMessage = "파일 사이즈가 xxx보다 작아야 합니다.";
        redirectAttributes.addAttribute("errorMessage", errorMessage);
        return "redirect:/admins/items/register";
    }
}
