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

import static springfox.bean.validators.plugins.BeanValidators.validatorFromBean;
import static springfox.bean.validators.plugins.BeanValidators.validatorFromField;

import javax.validation.constraints.NotNull;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;

@Component
@Order(BeanValidators.BEAN_VALIDATOR_PLUGIN_ORDER)
public class ModelPropertyNotNullAnnotationPlugin implements ModelPropertyBuilderPlugin {

    @Override
    public boolean supports(DocumentationType delimiter) {
        // we simply support all documentationTypes!
        return true;
    }

    @Override
    public void apply(ModelPropertyContext context) {
        Optional<NotNull> notNull = extractAnnotation(context);
        context.getBuilder().required(notNull.isPresent());
    }

    @VisibleForTesting
    Optional<NotNull> extractAnnotation(ModelPropertyContext context) {
        return validatorFromBean(context, NotNull.class).or(validatorFromField(context, NotNull.class));
    }

}
