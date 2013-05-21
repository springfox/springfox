package com.mangofactory.swagger.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An extension to the default Swagger @ApiError annotation.
 * <p/>
 * Allows documentation to be applied at the class level of the
 * declared exception.
 *
 * @author martypitt
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ApiErrors {
    Class<? extends Throwable>[] value() default { };

    ApiError[] errors() default { };
}
