package hochang.ecommerce.config;

import hochang.ecommerce.web.annotation.SignInUserArgumentResolver;
import hochang.ecommerce.web.interceptor.ItemSearchInterceptor;
import hochang.ecommerce.web.interceptor.LogInterceptor;
import hochang.ecommerce.web.interceptor.SignInCheckInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

import java.time.Duration;
import java.util.List;

import static hochang.ecommerce.constants.NumberConstants.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .order(INT_1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/js/**", "/*.ico", "/error");
        registry.addInterceptor(new SignInCheckInterceptor())
                .order(INT_2)
                .addPathPatterns("/users/**", "/admins/**")
                .excludePathPatterns("/css/**", "/js/**", "/*.ico", "/error");
        WebContentInterceptor webContentInterceptor = getCacheControlInterceptor();
        registry.addInterceptor(webContentInterceptor)
                .order(INT_3);
        registry.addInterceptor(new ItemSearchInterceptor())
                .order(INT_4)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/js/**", "/*.ico", "/error");

    }

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
        FilterRegistrationBean<ShallowEtagHeaderFilter> filterRegistrationBean
                = new FilterRegistrationBean<>(new ShallowEtagHeaderFilter());
        filterRegistrationBean.addUrlPatterns("/images/*");
        filterRegistrationBean.setName("ETagFilter");
        return filterRegistrationBean;
    }

    private WebContentInterceptor getCacheControlInterceptor() {
        CacheControl cacheControl = CacheControl
                .maxAge(Duration.ofSeconds(LONG_10))
                .sMaxAge(Duration.ofSeconds(LONG_60));
        WebContentInterceptor webContentInterceptor = new WebContentInterceptor();
        webContentInterceptor.addCacheMapping(cacheControl, "/images/**");
        return webContentInterceptor;
    }


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new SignInUserArgumentResolver());
    }
}
