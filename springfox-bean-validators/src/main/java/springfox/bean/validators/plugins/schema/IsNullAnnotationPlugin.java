/*
 * NullAnnotationPlugin.java
 *
 * (c) Copyright AUDI AG, 2019
 * All Rights reserved.
 *
 * AUDI AG
 * 85057 Ingolstadt
 * Germany
 */
package springfox.bean.validators.plugins.schema;


import javax.validation.constraints.Null;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.bean.validators.plugins.Validators;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import static springfox.bean.validators.plugins.Validators.annotationFromBean;
import static springfox.bean.validators.plugins.Validators.annotationFromField;

@Component
@Order(Validators.BEAN_VALIDATOR_PLUGIN_ORDER)
public class IsNullAnnotationPlugin implements ModelPropertyBuilderPlugin {

    /**
     * support all documentationTypes
     */
    @Override
    public boolean supports(DocumentationType delimiter) {
        // we simply support all documentationTypes!
        return true;
    }

    /**
     * read NotNull annotation
     */
    @Override
    public void apply(ModelPropertyContext context) {
        Optional<Null> isNull = extractAnnotation(context);
        if (isNull.isPresent()) {
            context.getBuilder().readOnly(isNull.isPresent());
        }
    }

    @VisibleForTesting
    Optional<Null> extractAnnotation(ModelPropertyContext context) {
        return annotationFromBean(context, Null.class).or(annotationFromField(context, Null.class));
    }
}
