package springfox.documentation.schema;

import java.lang.annotation.Annotation;
import java.util.List;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelProjectionProviderPlugin;

public class JacksonJsonViewProjectionProvider implements ModelProjectionProviderPlugin {

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  @Override
  public Optional<Class<?>> projectionFor(ResolvedType type, List<Annotation> typeAnnotations) {
    Optional<JsonView> jsonView = FluentIterable.from(typeAnnotations).filter(JsonView.class).first();
    if (jsonView.isPresent() && jsonView.get().value().length > 0) {
      return Optional.<Class<?>>of(jsonView.get().value()[0]);
    }
    return Optional.absent();
  }
}