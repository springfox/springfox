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
import java.lang.reflect.Field;

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

import static springfox.bean.validators.plugins.BeanValidators.validatorFromParameterExpansionField;

@Component
//@Order(BeanValidators.BEAN_VALIDATOR_PLUGIN_ORDER)
@Order(Ordered.LOWEST_PRECEDENCE)
public class ExpandedParameterDescriptionKeysAnnotationPlugin implements ExpandedParameterBuilderPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(ExpandedParameterDescriptionKeysAnnotationPlugin.class);

    @Autowired
    ApiDescriptionPropertiesReader propertiesReader;
    
    /**
     * support all documentationTypes
     */
    @Override
    public boolean supports(DocumentationType delimiter) {
        // we simply support all documentationTypes!
        return true;
    }

    /**
     * read description from Properties file if key is present
     */
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

    /**
     * read ApiParam-annotation from bean/field
     * @param context 
     * @return
     */
    @VisibleForTesting
    Optional<ApiParam> extractAnnotation(ParameterExpansionContext context) {
        return validatorFromParameterExpansionField(context, ApiParam.class);
    }


}
