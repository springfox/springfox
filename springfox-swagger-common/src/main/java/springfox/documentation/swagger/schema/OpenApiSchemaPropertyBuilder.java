package springfox.documentation.swagger.schema;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.property.ModelSpecificationFactory;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.spring.web.DescriptionResolver;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.*;
import static org.springframework.util.StringUtils.*;
import static springfox.documentation.schema.Annotations.*;
import static springfox.documentation.schema.ResolvedTypes.*;

@Component
@Order(SwaggerPluginSupport.OAS_PLUGIN_ORDER)
public class OpenApiSchemaPropertyBuilder implements ModelPropertyBuilderPlugin {
  private final DescriptionResolver descriptions;
  private final ModelSpecificationFactory modelSpecifications;

  @Autowired
  public OpenApiSchemaPropertyBuilder(
      DescriptionResolver descriptions,
      ModelSpecificationFactory modelSpecifications) {
    this.descriptions = descriptions;
    this.modelSpecifications = modelSpecifications;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void apply(ModelPropertyContext context) {
    Schema annotation = context.getAnnotatedElement()
                               .flatMap(OpenApiSchemaPropertyBuilder::findSchemaAnnotation)
                               .orElse(context.getBeanPropertyDefinition()
                                              .flatMap(b -> findPropertyAnnotation(b, Schema.class))
                                              .orElse(null));

    if (annotation != null) {
      ModelSpecification modelSpecification = null;
      ResolvedType type = toType(context.getResolver()).apply(annotation);
      if (!isVoid(type)) {
        modelSpecification =
            modelSpecifications.create(context.getOwner(), type);
      }

      context.getSpecificationBuilder()
             .description(toDescription(descriptions).apply(annotation))
             .readOnly(annotation.readOnly())
             .isHidden(annotation.hidden())
             .type(modelSpecification)
             .position(0)
             .required(annotation.required())
             .example(annotation.example())
             .enumerationFacet(e -> e.allowedValues(Arrays.asList(annotation.allowableValues())));

    }
  }


  static Optional<Schema> findSchemaAnnotation(AnnotatedElement annotated) {
    Optional<Schema> annotation = empty();

    if (annotated instanceof Method) {
      // If the annotated element is a method we can use this information to check superclasses as well
      annotation = ofNullable(AnnotationUtils.findAnnotation(
          ((Method) annotated), Schema.class));
    }

    return annotation.map(Optional::of)
                     .orElse(ofNullable(AnnotationUtils.getAnnotation(annotated, Schema.class)));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }

  static Function<Schema, ResolvedType> toType(final TypeResolver resolver) {
    return annotation -> resolver.resolve(annotation.implementation());
  }

  static Function<Schema, String> toDescription(
      final DescriptionResolver descriptions) {

    return annotation -> {
      String description = "";
      if (!isEmpty(annotation.description())) {
        description = annotation.description();
      }
      return descriptions.resolve(description);
    };
  }
}
