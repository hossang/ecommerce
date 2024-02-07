package hochang.ecommerce.config;

import hochang.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private UserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        HandlerMappingIntrospector handlerMappingIntrospector = new HandlerMappingIntrospector();
        http
                .csrf(c -> {
                    MvcRequestMatcher mvcRequestMatcher = new MvcRequestMatcher(handlerMappingIntrospector,
                            "/actuator/loggers/**");
                    c.ignoringRequestMatchers(mvcRequestMatcher);
                });
        http
                .formLogin()
                .loginPage("/sign-in")
                .successHandler(customAuthenticationSuccessHandler())
                .usernameParameter("username")
                .failureUrl("/sign-in?error=true")
                .and()
                .logout()
                .logoutRequestMatcher(new MvcRequestMatcher(handlerMappingIntrospector, "/users/*/sign-out"))
                .logoutSuccessUrl("/");
        http
                .authorizeRequests()
                .mvcMatchers("/admins/**").hasRole("ADMIN")
                .mvcMatchers("/users/**").hasAnyRole("ADMIN", "USER")
                .anyRequest().permitAll();
        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }
}
