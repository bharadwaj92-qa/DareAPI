package ogs.selenium.factory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ogs.selenium.web.ElementImpl;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImplementedBy {
    /**
     * Class implementing the interface. (by default)
     */
    Class<?> value() default ElementImpl.class;
}
