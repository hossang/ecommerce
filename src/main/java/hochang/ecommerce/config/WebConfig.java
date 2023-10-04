package hochang.ecommerce.config;

import hochang.ecommerce.constants.NumberConstants;
import hochang.ecommerce.web.annotation.SignInUserArgumentResolver;
import hochang.ecommerce.web.interceptor.LogInterceptor;
import hochang.ecommerce.web.interceptor.SignInCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static hochang.ecommerce.constants.NumberConstants.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .order(INT_1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/js/**");
        registry.addInterceptor(new SignInCheckInterceptor())
                .order(INT_2)
                .addPathPatterns("/users/**", "/admins/**")
                .excludePathPatterns("/css/**", "/js/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new SignInUserArgumentResolver());
    }
}
