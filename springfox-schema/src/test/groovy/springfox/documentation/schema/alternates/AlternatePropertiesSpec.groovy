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

package springfox.documentation.schema.alternates

import com.fasterxml.classmate.TypeResolver
import org.springframework.http.ResponseEntity
import spock.lang.Shared
import spock.lang.Specification
import springfox.documentation.schema.AlternateTypeRule
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.Model
import springfox.documentation.schema.ModelProvider
import springfox.documentation.schema.mixins.ModelProviderSupport

import static java.util.Collections.*
import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

class AlternatePropertiesSpec extends Specification implements ModelProviderSupport {
  @Shared def resolver = new TypeResolver()
  @Shared def namingStrategy = new DefaultGenericTypeNamingStrategy()

  def "Nested properties that have alternate types defined are rendered correctly"() {
    given:
    def provider = alternateTypeProvider()
    ModelProvider modelProvider = defaultModelProvider()
    Model model = modelProvider.modelFor(inputParam("0_0",
        "group",
        typeWithAlternateProperty(),
        Optional.empty(),
        new HashSet<>(),
        SWAGGER_12,
        provider,
        namingStrategy,
        emptySet())).get()
    expect:
    model.getName() == "TypeWithAlternateProperty"
    model.getProperties().containsKey("localDate")
    def modelProperty = model.getProperties().get("localDate")
    modelProperty.type.erasedType == java.sql.Date
    modelProperty.getQualifiedType() == "java.sql.Date"
    def item = modelProperty.getModelRef()
    item.type == "date"
    !item.collection
    item.itemType == null
  }

  def "ResponseEntity«Void» renders correctly when an alternate type is provided"() {
    given:
    def provider = alternateTypeProvider()
    provider.addRule(new AlternateTypeRule(resolver.resolve(ResponseEntity, Void), resolver.resolve(Void)))
    ModelProvider modelProvider = defaultModelProvider()
    Model model = modelProvider.modelFor(inputParam("0_0",
        "group",
        typeWithResponseEntityOfVoid(),
        Optional.empty(),
        new HashSet<>(),
        SWAGGER_12,
        provider,
        namingStrategy,
        emptySet())).get()
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
