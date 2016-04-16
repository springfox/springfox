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
package springfox.bean.apidescriptionreaders.plugins;

import io.swagger.annotations.ApiParam;

import java.lang.annotation.Annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;

@Component
//@Order(BeanValidators.BEAN_VALIDATOR_PLUGIN_ORDER)
@Order(Ordered.LOWEST_PRECEDENCE)
public class ParameterDescriptionKeysAnnotationPlugin implements ParameterBuilderPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(ParameterDescriptionKeysAnnotationPlugin.class);

    @Autowired

    ApiDescriptionPropertiesReader propertiesReader;
    
    @Override
    public boolean supports(DocumentationType delimiter) {
        // we simply support all documentationTypes!
        return true;
    }

    @Override
    public void apply(ParameterContext context) {
        LOG.info("*** apply parameter" );
        Optional<ApiParam> apiDescription = extractAnnotation(context);
           
        if (apiDescription.isPresent()) {
            ApiParam apiModelProperty = apiDescription.get();
             
             String descriptionValue = apiModelProperty.value();
             LOG.info("*** searching for key: " + descriptionValue);
             String description = propertiesReader.getProperty(descriptionValue);
             
             if (description!=null) {
                 context.parameterBuilder().description(description);
             }
         }
    }
        


    @VisibleForTesting
    Optional<ApiParam> extractAnnotation(ParameterContext context) {
        return validatorFromField(context, ApiParam.class);
    }

    @VisibleForTesting
    Optional<RequestParam> extractRequestParamAnnotation(ParameterContext context) {
        return validatorFromField(context, RequestParam.class);
    }

    @VisibleForTesting
    Optional<PathVariable> extractPathVariableAnnotation(ParameterContext context) {
        return validatorFromField(context, PathVariable.class);
    }

    public static <T extends Annotation> Optional<T> validatorFromField(ParameterContext context, Class<T> annotationType) {

        MethodParameter methodParam = context.methodParameter();

        T annotatedElement = methodParam.getParameterAnnotation(annotationType);
        Optional<T> annotationValue = Optional.absent();
        if (annotatedElement != null) {
            annotationValue = Optional.fromNullable(annotatedElement);
        }
        return annotationValue;
    }

}
