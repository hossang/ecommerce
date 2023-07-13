package hochang.ecommerce.web.annotation;

import hochang.ecommerce.domain.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auth {

    public Role role() default Role.ADMINISTRATOR;
}
