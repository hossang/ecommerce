package hochang.ecommerce.web.annotation;

import hochang.ecommerce.constants.SessionConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
public class SignInUserArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("supportsParameter 실행");

        boolean hasSignInAnnotation = parameter.hasParameterAnnotation(SignIn.class);
        boolean hasSignType = String.class.isAssignableFrom(parameter.getParameterType());

        return hasSignInAnnotation && hasSignType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer
            , NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return session.getAttribute(SessionConstant.SIGN_IN_USER);
    }
}
