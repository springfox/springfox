package springfox.documentation.schema;

import static springfox.documentation.schema.ResolvedTypes.resolvedTypeSignature;

import java.lang.annotation.Annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ProjectionProviderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.RequestMappingContext;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class JacksonJsonViewProjectionProvider implements ProjectionProviderPlugin {
  
  private static final Logger LOG = LoggerFactory.getLogger(JacksonJsonViewProjectionProvider.class);

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
  public Optional<ResolvedType> projectionFor(ResolvedType type, ResolvedMethodParameter parameter) {
    return projectionFor(type, parameter.findAnnotation(JsonView.class));
  }
  
  @Override
  public Optional<ResolvedType> projectionFor(ResolvedType type, RequestMappingContext context) {
    return projectionFor(type, context.findAnnotation(JsonView.class));
  }
  
  @Override
  public Optional<ResolvedType> projectionFor(ResolvedType type, OperationContext context) {
    return projectionFor(type, context.findAnnotation(JsonView.class));
  }
  
  private Optional<ResolvedType> projectionFor(ResolvedType type, Optional<JsonView> annotation) {
    Optional<ResolvedType> projection = Optional.absent();
    if (annotation.isPresent()) {
      Class<?>[] projections = ((JsonView)(annotation.get())).value();
      projection = Optional.of(typeResolver.resolve(projections[0]));
      LOG.debug("Found projection {} for type {}", resolvedTypeSignature(projection.get()).or("<null>"), resolvedTypeSignature(type).or("<null>"));
    }
    return projection;
  }

  @Override
  public boolean applyProjection(ResolvedType activeProjection, ResolvedField field) {
    final Class<?> activeView = activeProjection.getErasedType();
    if (activeView != null) {
      Optional<? extends Annotation> annotation = FluentIterable.from(field.getAnnotations())
          .filter(JsonView.class).first();
      if (!annotation.isPresent()) {
        return true;
      }
      final Class<?>[] typeProjections =  ((JsonView)(annotation.get())).value();
      int i = 0, len = typeProjections.length;
      for (; i < len; ++i) {
        if (typeProjections[i].isAssignableFrom(activeView)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public boolean applyProjection(ResolvedType activeProjection, Class<?>[] typeViews) {
    // TODO Auto-generated method stub
    return false;
  }
}