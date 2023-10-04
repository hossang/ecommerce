package hochang.ecommerce.exception;

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

    @ExceptionHandler(IllegalStateException.class)
    public String signUpDuplicateUser(IllegalStateException illegalStateException, RedirectAttributes redirectAttributes) {
        String errorMessage = illegalStateException.getMessage();
        redirectAttributes.addAttribute(ERROR_MESSAGE, "이미 존재하는 회원입니다.");
        return "redirect:/sign-up";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String outOfStock(IllegalArgumentException illegalArgumentException, HttpServletRequest request
            , RedirectAttributes redirectAttributes) {
        String referer = request.getHeader(REFERER);
        if (referer == null) {
            referer = "/";
        }
        //Item.reduceCount()에서 발새한 에러
        redirectAttributes.addAttribute(ERROR_MESSAGE, illegalArgumentException.getMessage());
        return "redirect:" + referer;
    }
}
