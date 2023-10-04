package hochang.ecommerce.web.interceptor;

import hochang.ecommerce.constants.SessionConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.StringTokenizer;

@Slf4j
public class SignInCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.info("인증 체크 인터셉터 실행 {}", requestURI);
        String username = extractUsernameFromRequestURI(requestURI);

        HttpSession session = request.getSession(false);
        if (!session.getAttribute(SessionConstant.SIGN_IN_USER).equals(username)) {
            log.info("미인증 사용자 요청");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
            return false;
        }
        return true;
    }

    private String extractUsernameFromRequestURI(String requestURI) {
        StringTokenizer stringTokenizer = new StringTokenizer(requestURI, "/");
        stringTokenizer.nextToken();
        return stringTokenizer.nextToken();
    }
}
