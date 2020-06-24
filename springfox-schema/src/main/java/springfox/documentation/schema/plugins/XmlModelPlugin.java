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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.JaxbPresentInClassPathCondition;
import springfox.documentation.schema.Xml;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelContext;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Component
@Conditional(JaxbPresentInClassPathCondition.class)
@SuppressWarnings("deprecation")
public class XmlModelPlugin implements ModelBuilderPlugin {
  private final TypeResolver typeResolver;

  @Autowired
  public XmlModelPlugin(TypeResolver typeResolver) {
    this.typeResolver = typeResolver;
  }

  @Override
  public void apply(ModelContext context) {
    XmlType annotation = AnnotationUtils.findAnnotation(forClass(context), XmlType.class);
    if (annotation != null) {
      context.getBuilder().xml(buildXml(annotation));
      context.getModelSpecificationBuilder()
          .facets(f -> f.xml(buildXml(annotation)));
    }
    XmlRootElement root = AnnotationUtils.findAnnotation(forClass(context), XmlRootElement.class);
    if (root != null) {
      context.getBuilder().xml(buildXml(root));
      context.getModelSpecificationBuilder()
          .facets(f -> f.xml(buildXml(root)));
    }
  }

  private Xml buildXml(XmlType annotation) {
    return new Xml()
        .name(defaultToNull(annotation.name()))
        .attribute(false)
        .namespace(defaultToNull(annotation.namespace()))
        .wrapped(false);
  }

  private Xml buildXml(XmlRootElement annotation) {
    return new Xml()
        .name(defaultToNull(annotation.name()))
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
