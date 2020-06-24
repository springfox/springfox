/*
 *
 *  Copyright 2017-2019 the original author or authors.
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
package springfox.springconfig;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ParameterStyle;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.Optional;

// tag::parameter-builder-plugin[]
@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 1000) //<1>
public class VersionApiReader implements ParameterBuilderPlugin {
  private TypeResolver resolver;

  public VersionApiReader(TypeResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public void apply(ParameterContext parameterContext) {
    ResolvedMethodParameter methodParameter = parameterContext.resolvedMethodParameter();
    Optional<VersionApi> requestParam = methodParameter.findAnnotation(VersionApi.class);
    if (requestParam.isPresent()) { //<2>
      parameterContext.requestParameterBuilder()
          .in(ParameterType.HEADER)
          .name("v")
          .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING))); //<3>
      parameterContext.requestParameterBuilder()
          .in(ParameterType.HEADER)
          .name("v")
          .query(q -> q.style(ParameterStyle.SIMPLE)
              .model(m -> m.scalarModel(ScalarType.STRING)));
    }
  }

  @Override
  public boolean supports(DocumentationType documentationType) {
    return true; //<4>
  }
}
// end::parameter-builder-plugin[]
