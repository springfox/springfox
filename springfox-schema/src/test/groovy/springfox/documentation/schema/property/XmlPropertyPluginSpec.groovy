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
import spock.lang.Specification
import springfox.documentation.builders.ModelPropertyBuilder
import springfox.documentation.builders.PropertySpecificationBuilder
import springfox.documentation.schema.plugins.XmlModelPlugin
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.contexts.ModelContext
import springfox.documentation.spi.schema.contexts.ModelPropertyContext

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlType


class XmlPropertyPluginSpec extends Specification {
  def "Should support all swagger documentation types"() {
    given:
    def sut = new XmlModelPlugin()

    expect:
    sut.supports(DocumentationType.SPRING_WEB)
    sut.supports(DocumentationType.SWAGGER_12)
    sut.supports(DocumentationType.SWAGGER_2)
  }

  def "When XmlPropertyPlugin has absent bean definition"() {
    when:
    def plugin = new XmlPropertyPlugin()
    def property = XmlTypeModel.getDeclaredField("strings")
    def context = new ModelPropertyContext(
        new ModelPropertyBuilder(), new PropertySpecificationBuilder("strings"),
        property,
        new TypeResolver(),
        Mock(ModelContext))
    plugin.apply(context)

    then:
    context.getBuilder().build().xml == null
  }

  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(
      name = "XML_TYPE_OBJECT",
      propOrder = ["strings"]
  )
  class XmlTypeModel implements Serializable {
    @XmlElement(name = "strings")
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
