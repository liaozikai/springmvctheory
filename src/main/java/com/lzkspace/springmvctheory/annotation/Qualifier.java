package com.lzkspace.springmvctheory.annotation;

import java.lang.annotation.*;

/**
 * @author : liaozikai
 * file : Autowired.java
 */
@Documented
@Target({ElementType.FIELD,ElementType.METHOD}) // 作用于字段上和方法上
@Retention(RetentionPolicy.RUNTIME)
public @interface Qualifier {
    String value();
}
