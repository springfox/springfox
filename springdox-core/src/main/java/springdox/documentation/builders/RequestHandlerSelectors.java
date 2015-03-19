package springdox.documentation.builders;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.core.annotation.AnnotationUtils;
import springdox.documentation.RequestHandler;

import java.lang.annotation.Annotation;

public class RequestHandlerSelectors {
  private RequestHandlerSelectors() {
    throw new UnsupportedOperationException();
  }

  public static Predicate<RequestHandler> any() {
    return Predicates.alwaysTrue();
  }

  public static Predicate<RequestHandler> none() {
    return Predicates.alwaysFalse();
  }

  public static Predicate<RequestHandler> withMethodAnnotation(final Class<? extends Annotation> annotation) {
    return new Predicate<RequestHandler>() {
      @Override
      public boolean apply(RequestHandler input) {
        return null != AnnotationUtils.findAnnotation(input.getHandlerMethod().getMethod(), annotation);
      }
    };
  }

  public static Predicate<RequestHandler> withClassAnnotation(final Class<? extends Annotation> annotation) {
    return new Predicate<RequestHandler>() {
      @Override
      public boolean apply(RequestHandler input) {
        return declaringClass(input).isAnnotationPresent(annotation);
      }
    };
  }

  private static Class<?> declaringClass(RequestHandler input) {
    return input.getHandlerMethod().getMethod().getDeclaringClass();
  }

}
