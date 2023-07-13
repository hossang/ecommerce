package hochang.ecommerce.config;

import hochang.ecommerce.web.annotation.SignInUserArgumentResolver;
import hochang.ecommerce.web.interceptor.AuthInterceptor;
import hochang.ecommerce.web.interceptor.LogInterceptor;
import hochang.ecommerce.web.interceptor.SignInCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/js/**");
        registry.addInterceptor(new SignInCheckInterceptor())
                .order(2)
                .addPathPatterns("/users/**", "/admins/**")
                .excludePathPatterns("/css/**", "/js/**");
        registry.addInterceptor(new AuthInterceptor())
                .order(3)
                .addPathPatterns("/users/**", "/admins/**")
                .excludePathPatterns("/css/**", "/js/**");

    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new SignInUserArgumentResolver());
    }
}
