package com.fashion.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestPart {
    String value() default "";
    boolean multiple() default false;
}
