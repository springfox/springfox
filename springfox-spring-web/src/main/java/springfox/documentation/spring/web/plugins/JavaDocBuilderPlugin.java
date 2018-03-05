/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package springfox.documentation.spring.web.plugins;

/**
 * Plugin to generate the @ApiParam and @ApiOperation values.
 */
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.ParameterContext;

/**
 * Swagger JavaDoc properties builder plugin.
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class JavaDocBuilderPlugin implements OperationBuilderPlugin, ParameterBuilderPlugin {

    private static final String PERIOD = ".";
    private static final String API_PARAM = "io.swagger.annotations.ApiParam";
    private static final String REQUEST_PARAM = "org.springframework.web.bind.annotation.RequestParam";
    private static final String PATH_VARIABLE = "org.springframework.web.bind.annotation.PathVariable";

    @Autowired
    private JavaDocPropertiesReader propertiesReader;

    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }

    @Override
    public void apply(OperationContext context) {

        String notes = context.requestMappingPattern() + PERIOD +
                context.httpMethod().toString() + ".notes";
        if (StringUtils.hasText(notes) && StringUtils.hasText(propertiesReader.getProperty(notes))) {
            context.operationBuilder().notes(propertiesReader.getProperty(notes));
        }
    }

    @Override
    public void apply(ParameterContext context) {
        Annotation apiParam = validatorFromField(context, API_PARAM);
        if (apiParam != null) {
            Optional<Boolean> isRequired = isParamRequired(apiParam, context);
            Optional<String> parmName = context.resolvedMethodParameter().defaultName();
            String description = null;
            if (!hasValue(apiParam, context) && parmName.isPresent()) {
                String key = context.getOperationContext().requestMappingPattern() + PERIOD +
                        context.getOperationContext().httpMethod().name() + ".param." + parmName.get();
                description = propertiesReader.getProperty(key);
            }
            if (description != null) {
                context.parameterBuilder().description(description);
            }
            if (isRequired.isPresent()) {
                context.parameterBuilder().required(isRequired.get());
            }
        }
    }

    @VisibleForTesting
    String extractApiParamDescription(Annotation annotation) {
        return annotation != null ? annotation.annotationType().getName() : null;
    }

    @VisibleForTesting
    Optional<Boolean> isParamRequired(Annotation apiParam, ParameterContext context) {
        if (apiParam != null) {
            Optional<Boolean> required = isRequired(apiParam, context);
            if (required.isPresent()) {
                return required;
            }
        }
        Annotation annotation = validatorFromField(context, REQUEST_PARAM);
        if (annotation == null) {
            annotation = validatorFromField(context, PATH_VARIABLE);
        }
        return annotation != null ? isRequired(annotation, context) : Optional.<Boolean>absent();
    }

    @VisibleForTesting
    Optional<Boolean> isRequired(Annotation annotation, ParameterContext context) {
        Boolean result = null;
        for (Method method : annotation.annotationType().getDeclaredMethods()) {
            if (method.getName().equals("required")) {
                try {
                    return Optional.of((Boolean) method.invoke(annotation, (Object) null));
                } catch (Exception ex) {
                    return Optional.absent();
                }
            }
        }
        return Optional.absent();
    }

    @VisibleForTesting
    boolean hasValue(Annotation annotation, ParameterContext context) {
        boolean result = false;
        for (Method method : annotation.annotationType().getDeclaredMethods()) {
            if (method.getName().equals("value")) {
                try {
                    Optional<String> value = Optional.of((String) method.invoke(annotation, (Object) null));
                    return value.isPresent();
                } catch (Exception ex) {
                    return false;
                }
            }
        }
        return false;
    }

    public static Annotation validatorFromField(
            ParameterContext context, String annotationType) {

        ResolvedMethodParameter methodParam = context.resolvedMethodParameter();

        for (Annotation annotation : methodParam.getAnnotations()) {
            if (annotation.annotationType().getName().equals(annotationType)) {
                return annotation;
            }
        }
        return null;

    }
}
