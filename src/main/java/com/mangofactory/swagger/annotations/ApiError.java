package com.mangofactory.swagger.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Drop-in replacement for {@link com.wordnik.swagger.annotations.ApiError}
 * that is also declarable at type level, for classes that extend {@link Throwable}.
 * <p/>
 * If declared on a type, then any Controller methods which are declared
 * to throw the exception will automatically have those extensions documented.
 *
 * @author martypitt
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface ApiError {

    int code();

    String reason();
}
