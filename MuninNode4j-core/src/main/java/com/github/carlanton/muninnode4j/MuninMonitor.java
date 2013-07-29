package com.github.carlanton.muninnode4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface MuninMonitor {
    // From http://munin-monitoring.org/wiki/protocol-config

    String title() default "";
    String args() default "";
    String category() default "";
    String info() default "";
    String order() default "";
    String scale() default "";
    String period() default "";
    String vlabel() default "";

    String[] labels() default {"value1"};
}
