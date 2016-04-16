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

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ExpandedParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;

@Component
//@Order(BeanValidators.BEAN_VALIDATOR_PLUGIN_ORDER)
@Order(Ordered.LOWEST_PRECEDENCE)
public class ExpandedParameterDescriptionKeysAnnotationPlugin implements ExpandedParameterBuilderPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(ExpandedParameterDescriptionKeysAnnotationPlugin.class);

	@Autowired
	ApiDescriptionPropertiesReader propertiesReader;
	
    @Override
    public boolean supports(DocumentationType delimiter) {
        // we simply support all documentationTypes!
        return true;
    }

    @Override
    public void apply(ParameterExpansionContext context) {
    	LOG.info("*** apply expanded parameter" );
    	Optional<ApiParam> apiDescription = extractAnnotation(context);
	   	Field myfield = context.getField();
        LOG.debug("myfield: " + myfield.getName());
        
	   	 if (apiDescription.isPresent()) {
	   		ApiParam apiModelProperty = apiDescription.get();
	   		 
	   		 String descriptionValue = apiModelProperty.value();
	   		 LOG.info("*** searching for key: " + descriptionValue);
	   		 String description = propertiesReader.getProperty(descriptionValue);
	   		 
	   		 if (description!=null) {
	   			 context.getParameterBuilder().description(description);
	   		 }
	   	 }

    }

    @VisibleForTesting
    Optional<ApiParam> extractAnnotation(ParameterExpansionContext context) {

        return validatorFromBean(context, ApiParam.class).or(validatorFromField(context, ApiParam.class));
    }

    public static <T extends Annotation> Optional<T> validatorFromBean(ParameterExpansionContext context, Class<T> annotationType) {

        Field field = context.getField();

        Optional<T> notNull = Optional.absent();
        if (field != null) {
            notNull = Optional.fromNullable(field.getAnnotation(annotationType));
        }
        return notNull;
    }

    public static <T extends Annotation> Optional<T> validatorFromField(ParameterExpansionContext context, Class<T> annotationType) {

        Field field = context.getField();
        Optional<T> notNull = Optional.absent();
        if (field != null) {
            LOG.debug("Annotation size present for field " + field.getName() + "!!");
            notNull = Optional.fromNullable(field.getAnnotation(annotationType));
        }

        return notNull;
    }

}
