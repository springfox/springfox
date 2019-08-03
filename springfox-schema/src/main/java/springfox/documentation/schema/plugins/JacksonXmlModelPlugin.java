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
package springfox.documentation.schema.plugins;

import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.JacksonXmlPresentInClassPathCondition;
import springfox.documentation.schema.Xml;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelContext;

@Component
@Conditional(JacksonXmlPresentInClassPathCondition.class)
public class JacksonXmlModelPlugin implements ModelBuilderPlugin {
  private final TypeResolver typeResolver;

  @Autowired
  public JacksonXmlModelPlugin(TypeResolver typeResolver) {
    this.typeResolver = typeResolver;
  }

  @Override
  public void apply(ModelContext context) {
    JacksonXmlRootElement root = AnnotationUtils.findAnnotation(forClass(context), JacksonXmlRootElement.class);
    if (root != null) {
      context.getBuilder().xml(buildXml(root));
    }
  }

  private Xml buildXml(JacksonXmlRootElement annotation) {
    return new Xml()
        .name(defaultToNull(annotation.localName()))
        .attribute(false)
        .namespace(defaultToNull(annotation.namespace()))
        .wrapped(false);
  }

  private String defaultToNull(String value) {
    return "##default".equalsIgnoreCase(value) ? null : value;
  }

  private Class<?> forClass(ModelContext context) {
    return typeResolver.resolve(context.getType()).getErasedType();
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
