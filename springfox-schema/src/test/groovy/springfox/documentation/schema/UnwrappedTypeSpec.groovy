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

package springfox.documentation.schema

import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.schema.mixins.ModelProviderSupport
import springfox.documentation.schema.mixins.ConfiguredObjectMapperSupport
import springfox.documentation.schema.mixins.TypesForTestingSupport
import springfox.documentation.spi.schema.contexts.ModelContext

import static springfox.documentation.spi.DocumentationType.*

@Mixin([TypesForTestingSupport, ModelProviderSupport, ConfiguredObjectMapperSupport, AlternateTypesSupport])
class UnwrappedTypeSpec extends Specification {
  @Unroll("Unwrapped types are rendered correctly for #typeOfOM")
  def "Unwrapped types are rendered correctly"() {
    given:
      def provider = defaultModelProvider(objectMapperToUse)
    def namingStrategy = new DefaultGenericTypeNamingStrategy()
      Model asInput = provider.modelFor(ModelContext.inputParam(unwrappedType(), SWAGGER_12, alternateTypeProvider(),
              namingStrategy)).get()
      Model asReturn = provider.modelFor(ModelContext.returnValue(unwrappedType(), SWAGGER_12, alternateTypeProvider
              (), namingStrategy)).get()

    expect:
      asInput.getName() == "UnwrappedType"
      asInput.getProperties().size() == 1
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getQualifiedType() == qualifiedType
      def item = modelProperty.getModelRef()
      item.type == "string"
      !item.collection
      item.itemType == null

      asReturn.getName() == "UnwrappedType"
      asReturn.getProperties().size() == 1
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.type.erasedType == type
      retModelProperty.getQualifiedType() == qualifiedType
      def retItem = retModelProperty.getModelRef()
      retItem.type == "string"
      !retItem.collection
      retItem.itemType == null

    where:
      property    | type    | qualifiedType       | objectMapperToUse             | typeOfOM
      "name"      | String  | "java.lang.String"  | objectMapperThatUsesFields()  | "fields"
      "name"      | String  | "java.lang.String"  | objectMapperThatUsesGetters() | "getters"
      "name"      | String  | "java.lang.String"  | objectMapperThatUsesSetters() | "setters"
  }
}
