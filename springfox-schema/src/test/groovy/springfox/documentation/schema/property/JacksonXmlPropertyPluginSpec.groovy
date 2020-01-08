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
package springfox.documentation.schema.property

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import spock.lang.Specification
import springfox.documentation.builders.ModelPropertyBuilder
import springfox.documentation.builders.PropertySpecificationBuilder
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.contexts.ModelPropertyContext

class JacksonXmlPropertyPluginSpec extends Specification {
  def "Should support all swagger documentation types"() {
    given:
    def sut = new JacksonXmlPropertyPlugin()

    expect:
    sut.supports(DocumentationType.SPRING_WEB)
    sut.supports(DocumentationType.SWAGGER_12)
    sut.supports(DocumentationType.SWAGGER_2)
  }

  def "When JacksonXmlModel then create not wrapped xml element with name \"strings-list\""() {
    when:
    def plugin = new JacksonXmlPropertyPlugin()
    def property = JacksonXmlModel.getDeclaredField("strings")
    def context = new ModelPropertyContext(
        new ModelPropertyBuilder(),
        new PropertySpecificationBuilder(),
        property,
        new TypeResolver(),
        DocumentationType.SWAGGER_2)
    plugin.apply(context)

    then:
    context.getBuilder().build().xml?.name == "strings-list"
    context.getBuilder().build().xml?.prefix == null
    !context.getBuilder().build().xml?.attribute
    !context.getBuilder().build().xml?.wrapped
  }

  def "When JacksonXmlAttributeModel then create not wrapped xml attribute with name \"string-attribute\""() {
    when:
    def plugin = new JacksonXmlPropertyPlugin()
    def property = JacksonXmlAttributeModel.getDeclaredField("string")
    def context = new ModelPropertyContext(
        new ModelPropertyBuilder(),
        new PropertySpecificationBuilder(),
        property,
        new TypeResolver(),
        DocumentationType.SWAGGER_2)
    plugin.apply(context)

    then:
    context.getBuilder().build().xml?.name == "string-attribute"
    context.getBuilder().build().xml?.prefix == null
    context.getBuilder().build().xml?.attribute
    !context.getBuilder().build().xml?.wrapped
  }

  def "When JacksonXmlWrapperModel then create wrapped xml element with name \"strings-list\""() {
    when:
    def plugin = new JacksonXmlPropertyPlugin()
    def property = JacksonXmlWrapperModel.getDeclaredField("strings")
    def context = new ModelPropertyContext(
        new ModelPropertyBuilder(),
        new PropertySpecificationBuilder(),
        property,
        new TypeResolver(),
        DocumentationType.SWAGGER_2)
    plugin.apply(context)

    then:
    context.getBuilder().build().xml?.name == "strings-list"
    context.getBuilder().build().xml?.prefix == null
    !context.getBuilder().build().xml?.attribute
    context.getBuilder().build().xml?.wrapped
  }

  @JacksonXmlRootElement
  class JacksonXmlModel implements Serializable {
    @JacksonXmlProperty(localName = "strings-list", isAttribute = false)
    private List<String> strings

    List<String> getStrings() {
      if (this.strings == null) {
        this.strings = new ArrayList<>()
      }
      return this.strings
    }

    void setStrings(List<String> strings) {
      this.strings = strings
    }
  }

  @JacksonXmlRootElement
  class JacksonXmlAttributeModel implements Serializable {
    @JacksonXmlProperty(localName = "string-attribute", isAttribute = true)
    private String string

    String getString() {
      return this.string
    }

    void setString(String string) {
      this.string = string
    }
  }

  @JacksonXmlRootElement
  class JacksonXmlWrapperModel implements Serializable {
    @JacksonXmlElementWrapper(localName = "strings-list", useWrapping = true)
    @JacksonXmlProperty(localName = "string", isAttribute = false)
    private List<String> strings

    List<String> getStrings() {
      if (this.strings == null) {
        this.strings = new ArrayList<>()
      }
      return this.strings
    }

    void setStrings(List<String> strings) {
      this.strings = strings
    }
  }
}
