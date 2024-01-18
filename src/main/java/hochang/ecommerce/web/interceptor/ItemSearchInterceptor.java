package hochang.ecommerce.web.interceptor;

import hochang.ecommerce.dto.ItemSearch;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ItemSearchInterceptor implements HandlerInterceptor {
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView instanceof ModelAndView) {
            modelAndView.addObject("itemSearch", new ItemSearch());
        }
    }
}
