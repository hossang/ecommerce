package hochang.ecommerce.web.interceptor;

import hochang.ecommerce.domain.Role;
import hochang.ecommerce.web.annotation.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
public class AuthInterceptor implements HandlerInterceptor {
    private static final String ADMIN_USER = "dlghckd1";
    private static final String KEY = "username";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod == false) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        Auth auth = handlerMethod.getMethodAnnotation(Auth.class);
        if (auth == null) {
            return true;
        }

        Role role = auth.role();
        if (role.equals(Role.ADMINISTRATOR)) {
            Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            String username = (String) pathVariables.get(KEY);
            log.info("username = {}", username);
            if (!username.equals(ADMIN_USER)) {
                response.sendRedirect(request.getContextPath());
                return false;
            }
        }

        return true;
    }
}
