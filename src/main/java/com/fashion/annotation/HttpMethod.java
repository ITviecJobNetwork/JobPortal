package com.fashion.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpMethod {

    String value() default "";

    Method method() default Method.GET;

    enum Method {
        GET, POST
    }
}
