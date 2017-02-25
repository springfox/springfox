/*
 *
 *  Copyright 2015-2017 the original author or authors.
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

import com.google.common.base.Optional;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import static springfox.bean.validators.plugins.Validators.*;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class ModelPropertyDescriptionPlugin implements ModelPropertyBuilderPlugin {
  private static final Logger LOG = LoggerFactory.getLogger(ModelPropertyDescriptionPlugin.class);

  private final DescriptionResolver propertiesReader;

  @Autowired
  public ModelPropertyDescriptionPlugin(DescriptionResolver propertiesReader) {
    this.propertiesReader = propertiesReader;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    // we simply support all documentationTypes!
    return true;
  }

  @Override
  public void apply(ModelPropertyContext context) {
    Optional<ApiModelProperty> apiModelProperty = annotationFromBean(context, ApiModelProperty.class)
        .or(annotationFromField(context, ApiModelProperty.class));

    if (apiModelProperty.isPresent()) {
      ApiModelProperty annotation = apiModelProperty.get();
      String key = annotation.value();
      LOG.debug("Searching for description with key: {}", key);
      context.getBuilder().description(propertiesReader.resolve(key));
    }
  }
}
