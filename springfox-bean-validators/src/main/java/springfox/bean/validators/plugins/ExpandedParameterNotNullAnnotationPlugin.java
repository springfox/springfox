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
import java.lang.reflect.Field;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ExpandedParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;

@Component
@Order(BeanValidators.BEAN_VALIDATOR_PLUGIN_ORDER)
public class ExpandedParameterNotNullAnnotationPlugin implements ExpandedParameterBuilderPlugin {

	private static final Logger LOG = LoggerFactory.getLogger(ExpandedParameterNotNullAnnotationPlugin.class);

	@Override
	public boolean supports(DocumentationType delimiter) {
		// we simply support all documentationTypes!
		return true;
	}

	@Override
	public void apply(ParameterExpansionContext context) {
		Field myfield = context.getField();
		LOG.debug("myfield: " + myfield.getName());

		Optional<NotNull> size = extractAnnotation(context);

		if (size.isPresent()) {
			LOG.info("myfield: " + myfield.getName() + " set to required!!");
			context.getParameterBuilder().required(true);

		}
	}

	@VisibleForTesting
	Optional<NotNull> extractAnnotation(ParameterExpansionContext context) {

		return validatorFromBean(context, NotNull.class).or(validatorFromField(context, NotNull.class));
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
