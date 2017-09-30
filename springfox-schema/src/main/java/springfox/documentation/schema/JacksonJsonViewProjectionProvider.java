package springfox.documentation.schema;

import java.lang.annotation.Annotation;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Function;
import com.google.common.base.Optional;

import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ProjectionProviderPlugin;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;

@Component
@Order()
public class JacksonJsonViewProjectionProvider implements ProjectionProviderPlugin {
  
  private static final Class<JsonView> requiredAnnotation = JsonView.class;

  private final TypeResolver typeResolver;
  
  @Autowired
  public JacksonJsonViewProjectionProvider(TypeResolver typeResolver) {
    this.typeResolver = typeResolver;
  }
  
  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
  
  @Override
  public Optional<Class<? extends Annotation>> getRequiredAnnotation() {
    return Optional.<Class<? extends Annotation>>of(requiredAnnotation);
  }

  @Override
  public List<ResolvedType> projectionsFor(ResolvedType type, Optional<? extends Annotation> requiredAnnotation) {
    List<Class<?>> projections = newArrayList();
    if (requiredAnnotation.isPresent() &&
        requiredAnnotation.get() instanceof JsonView) {
      projections = newArrayList(((JsonView)requiredAnnotation.get()).value());
    }
    return from(projections).transform(toResolvedType()).toList();
  }

  @Override
  public boolean applyProjection(ResolvedType activeProjection, ResolvedType typeToApply,
      Optional<? extends Annotation> requiredAnnotation) {
    final Class<?> activeView = activeProjection.getErasedType();
    if (requiredAnnotation.isPresent() &&
        requiredAnnotation.get() instanceof JsonView &&
        activeView != null) {
      final Class<?>[] typeProjections = ((JsonView)requiredAnnotation.get()).value();
      int i = 0, len = typeProjections.length;
      for (; i < len; ++i) {
        if (typeProjections[i].isAssignableFrom(activeView)) {
          return true;
        }
      }
    }
    return false;
  }
  
  private Function<Class<?>, ResolvedType> toResolvedType() {
    return new Function<Class<?>, ResolvedType>() {
      @Override
      public ResolvedType apply(Class<?> input) {
        return typeResolver.resolve(input);
      }
    };
  }
}