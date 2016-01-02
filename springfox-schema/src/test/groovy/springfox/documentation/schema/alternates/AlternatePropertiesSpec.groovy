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

package springfox.documentation.schema.alternates
import org.joda.time.LocalDate
import org.springframework.http.ResponseEntity
import spock.lang.Specification
import springfox.documentation.schema.*
import springfox.documentation.schema.mixins.ModelProviderSupport
import springfox.documentation.schema.mixins.TypesForTestingSupport

import static springfox.documentation.schema.AlternateTypeRules.*
import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([ModelProviderSupport, TypesForTestingSupport, AlternateTypesSupport])
class AlternatePropertiesSpec extends Specification {
  def namingStrategy = new DefaultGenericTypeNamingStrategy()
  def "Nested properties that have alternate types defined are rendered correctly" () {
    given:
      def provider = alternateTypeProvider()
      provider.addRule(newRule(LocalDate, String))
      ModelProvider modelProvider = defaultModelProvider()
      Model model = modelProvider.modelFor(inputParam(typeWithAlternateProperty(), SWAGGER_12, provider, namingStrategy)).get()
    expect:
      model.getName() == "TypeWithAlternateProperty"
      model.getProperties().containsKey("localDate")
      def modelProperty = model.getProperties().get("localDate")
      modelProperty.type.erasedType == String
      modelProperty.getQualifiedType() == "java.lang.String"
      def item = modelProperty.getModelRef()
      item.type == "string"
      !item.collection
      item.itemType == null
  }

  def "ResponseEntity«Void» renders correctly when an alternate type is provided" () {
    given:
      def provider = alternateTypeProvider()
      provider.addRule(new AlternateTypeRule(resolver.resolve(ResponseEntity, Void), resolver.resolve(Void)))
      ModelProvider modelProvider = defaultModelProvider()
      Model model = modelProvider.modelFor(inputParam(typeWithResponseEntityOfVoid(), SWAGGER_12,
              alternateTypeProvider(), namingStrategy)).get()
    expect:
      model.getName() == "GenericType«ResponseEntity«Void»»"
      model.getProperties().containsKey("genericField")
      def modelProperty = model.getProperties().get("genericField")
      modelProperty.type.erasedType == Void
      modelProperty.getQualifiedType() == "java.lang.Void"
      def item = modelProperty.getModelRef()
      item.type == "void"
      !item.collection
      item.itemType == null
  }
}
