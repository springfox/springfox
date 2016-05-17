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
package springfox.bean.validators.plugins;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.service.AllowablePatternValues;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import javax.validation.constraints.Pattern;

import static springfox.bean.validators.plugins.BeanValidators.*;

/**
 * Plugin to handle {@link Pattern} annotation.
 * <p>
 * If there are both annotations {@link Pattern} and {@link Pattern} assigned to one field or
 * getter/setter then the current handler will <b>always</b>
 * the result of {@link springfox.bean.validators.plugins.SizeAnnotationPlugin}.
 * This is implemented through order annotation. It has +1 priority than all other plugins defined in
 * {@link BeanValidatorPluginsConfiguration}. The behavior above can be examined in groovy unit test
 * {@link springfox.bean.validators.plugins.PatternAnnotationPluginSpec}
 *
 */
@Component
@Order(BeanValidators.BEAN_VALIDATOR_PLUGIN_ORDER + 1)
public class PatternAnnotationPlugin implements ModelPropertyBuilderPlugin {
    private static final Logger LOG = LoggerFactory.getLogger(PatternAnnotationPlugin.class);

    @Override
    public boolean supports(DocumentationType delimiter) {
        // we simply support all documentationTypes!
        return true;
    }

    @Override
    public void apply(ModelPropertyContext context) {
        Optional<Pattern> pattern = extractAnnotation(context);

        if (pattern.isPresent()) {
            context.getBuilder().allowableValues(createAllowableValuesFromPattern(pattern.get()));
        }
    }

    private AllowableValues createAllowableValuesFromPattern(Pattern pattern) {
        LOG.debug("@Size detected: adding MinLength/MaxLength to field");
        return new AllowablePatternValues(pattern.regexp());
    }


    @VisibleForTesting
    private Optional<Pattern> extractAnnotation(ModelPropertyContext context) {
        return validatorFromBean(context, Pattern.class)
                .or(validatorFromField(context, Pattern.class));
    }
}
