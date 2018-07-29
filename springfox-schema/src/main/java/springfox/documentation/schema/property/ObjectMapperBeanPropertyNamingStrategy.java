/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

package springfox.documentation.schema.property;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.configuration.ObjectMapperConfigured;

import java.util.Optional;

import static java.util.Optional.*;
import static springfox.documentation.schema.property.BeanPropertyDefinitions.*;

/**
 * BeanPropertyNamingStrategy based on ObjectMapper naming strategy.
 * Uses {@link com.fasterxml.jackson.databind.PropertyNamingStrategy} to name.
 * In case it cannot get information from property's getter or field, it returns the same current name.
 */
@Component
public class ObjectMapperBeanPropertyNamingStrategy implements BeanPropertyNamingStrategy,
        ApplicationListener<ObjectMapperConfigured> {

  private static final Logger LOG = LoggerFactory.getLogger(ObjectMapperBeanPropertyNamingStrategy.class);
  private ObjectMapper objectMapper;

  @Override
  public String nameForSerialization(final BeanPropertyDefinition beanProperty) {

    SerializationConfig serializationConfig = objectMapper.getSerializationConfig();

    Optional<PropertyNamingStrategy> namingStrategy
            = ofNullable(serializationConfig.getPropertyNamingStrategy());
    String newName = namingStrategy
            .map(overTheWireName(beanProperty, serializationConfig))
            .orElse(beanProperty.getName());

    LOG.debug("Name '{}' renamed to '{}'", beanProperty.getName(), newName);

    return newName;
  }

  @Override
  public String nameForDeserialization(final BeanPropertyDefinition beanProperty) {

    DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig();

    Optional<PropertyNamingStrategy> namingStrategy
            = ofNullable(deserializationConfig.getPropertyNamingStrategy());
    String newName = namingStrategy
            .map(overTheWireName(beanProperty, deserializationConfig))
            .orElse(beanProperty.getName());

    LOG.debug("Name '{}' renamed to '{}'", beanProperty.getName(), newName);

    return newName;
  }

  @Override
  public void onApplicationEvent(ObjectMapperConfigured event) {
    this.objectMapper = event.getObjectMapper();
  }
}
