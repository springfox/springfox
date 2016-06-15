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

import java.lang.annotation.Annotation;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import springfox.bean.validators.util.MinMaxUtil;
import springfox.documentation.service.AllowableRangeValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import static springfox.bean.validators.plugins.BeanValidators.validatorFromParameterField;
@Component
@Order(BeanValidators.BEAN_VALIDATOR_PLUGIN_ORDER)
public class ParameterMinMaxAnnotationPlugin implements ParameterBuilderPlugin {
    private static final Logger LOG = LoggerFactory.getLogger(ParameterMinMaxAnnotationPlugin.class);

    /**
     * support all documentationTypes
     */
    public boolean supports(DocumentationType delimiter) {
        // we simply support all documentationTypes!
        return true;
    }

    /** 
     * read Min/Max annotations
     */
    public void apply(ParameterContext context) {
        Optional<Min> min = extractMin(context);
        Optional<Max> max = extractMax(context);
        if (min.isPresent() || max.isPresent()) {
            AllowableRangeValues values = MinMaxUtil.createAllowableValuesFromMinMaxForNumbers(min, max);
            LOG.debug("adding allowable Values: " + values.getMin() + " - " + values.getMax());
            context.parameterBuilder().allowableValues(values);
            
        }
    }

    /**
     * extract Min from field
     * @param context
     * @return
     */
    @VisibleForTesting
    Optional<Min> extractMin(ParameterContext context) {
        return validatorFromParameterField(context, Min.class);
    }

    /**
     * extract Max from field
     * @param context
     * @return
     */
    @VisibleForTesting
    Optional<Max> extractMax(ParameterContext context) {
        return validatorFromParameterField(context, Max.class);
    }


}
