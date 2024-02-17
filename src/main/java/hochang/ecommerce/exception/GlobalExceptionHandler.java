package hochang.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String REFERER = "Referer";
    private static final int FILE_SIZE = 20;

    @ExceptionHandler(MultipartException.class)
    public String exceedFileSize(RedirectAttributes redirectAttributes, HttpServletRequest request) {
        String referer = request.getHeader(REFERER);
        if (referer == null) {
            referer = "/";
        }
        redirectAttributes.addAttribute(ERROR_MESSAGE, "파일 사이즈가 " + FILE_SIZE + "MB 미만이어" +
                "야 합니다.");
        return "redirect:" + referer;
    }

    @ExceptionHandler(UserIllegalStateException.class)
    public String signUpDuplicateUser(UserIllegalStateException userIllegalStateException,
                                      RedirectAttributes redirectAttributes) {
        String errorMessage = userIllegalStateException.getMessage();
        redirectAttributes.addAttribute(ERROR_MESSAGE, errorMessage);
        return "redirect:/sign-up";
    }

    @ExceptionHandler(AccountIllegalStateException.class)
    public ResponseEntity<String> saveDuplicateAccountNumber(AccountIllegalStateException accountIllegalStateException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(accountIllegalStateException.getMessage());
    }

    @ExceptionHandler(AccountIllegalArgumentException.class)
    public String shortOfBalance(AccountIllegalArgumentException accountIllegalArgumentException,
                                 RedirectAttributes redirectAttributes, HttpServletRequest request) {
        String referer = request.getHeader(REFERER);
        if (referer == null) {
            referer = "/";
        }
        redirectAttributes.addAttribute(ERROR_MESSAGE, accountIllegalArgumentException.getMessage());
        return "redirect:" + referer;
    }


    @ExceptionHandler(ItemIllegalArgumentException.class)
    public String outOfStock(ItemIllegalArgumentException itemIllegalArgumentException, HttpServletRequest request
            , RedirectAttributes redirectAttributes) {
        String referer = request.getHeader(REFERER);
        if (referer == null) {
            referer = "/";
        }
        redirectAttributes.addAttribute(ERROR_MESSAGE, itemIllegalArgumentException.getMessage());
        return "redirect:" + referer;
    }
}
