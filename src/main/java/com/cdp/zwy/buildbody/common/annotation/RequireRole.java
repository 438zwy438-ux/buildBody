package com.cdp.zwy.buildbody.common.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {
    
    String[] value() default {};
    
    boolean requireLogin() default true;
}