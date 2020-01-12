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
package springfox.documentation.swagger2.mappers

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.classmate.types.ResolvedObjectType
import io.swagger.models.properties.AbstractNumericProperty
import io.swagger.models.properties.ObjectProperty
import io.swagger.models.properties.RefProperty
import io.swagger.models.properties.StringProperty
import org.mapstruct.factory.Mappers
import spock.lang.Shared
import spock.lang.Unroll
import springfox.documentation.builders.ModelBuilder
import springfox.documentation.builders.ModelPropertyBuilder
import springfox.documentation.schema.CodeGenGenericTypeNamingStrategy
import springfox.documentation.schema.Model
import springfox.documentation.schema.ModelProperty
import springfox.documentation.schema.ModelRef
import springfox.documentation.schema.ModelReference
import springfox.documentation.schema.SchemaSpecification
import springfox.documentation.schema.SimpleType
import springfox.documentation.service.AllowableRangeValues
import springfox.documentation.spi.DocumentationType

import java.util.function.Function
import java.util.stream.Collectors

import static java.util.Collections.*
import static springfox.documentation.schema.ResolvedTypes.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*
import static springfox.documentation.swagger2.mappers.ModelMapper.*

class ModelMapperSpec extends SchemaSpecification {

  @Shared def namingStrategy = new CodeGenGenericTypeNamingStrategy()
  @Shared def resolver = new TypeResolver()

  def getIds = new Function<Model, String>() {
    String apply(Model model) {
      return model.getId()
    }
  }

  def "models are serialized correctly"() {
    given:
    Model model = modelProvider.modelFor(
        inputParam(
            "0_0",
            "group",
            resolver.resolve(typeToTest),
            Optional.empty(),
            new HashSet<>(),
            DocumentationType.SWAGGER_2,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).get()
    def modelMap = new HashMap<>()

    and:
    modelMap.put("test", model)

    when:
    def mapped = Mappers.getMapper(ModelMapper).mapModels(modelMap)

    then:
    mapped.containsKey("test")

    and:
    def mappedModel = mapped.get("test")
    mappedModel.properties.size() == model.properties.size()

    where:
    typeToTest << [simpleType(), mapsContainer(), enumType(), typeWithLists()]
  }

  def "void properties or collection of voids are filtered in the model"() {
    given:
    Model model = modelProvider.modelFor(
        inputParam(
            "0_0",
            "group",
            resolver.resolve(typeWithVoidLists()),
            Optional.empty(),
            new HashSet<>(),
            DocumentationType.SWAGGER_2,
            alternateTypeProvider(),
            namingStrategy,
            emptySet()))
        .get()
    def modelMap = new HashMap<>()

    and:
    modelMap.put("test", model)

    when:
    def mapped = Mappers.getMapper(ModelMapper).mapModels(modelMap)

    then:
    def mappedModel = mapped.get("test")
    model.properties.size() == 3
    mappedModel.properties == null
  }

  def "model dependences are inferred correctly for list of map of string to string"() {
    given:
    Map<ResolvedType, Model> modelMap = modelProvider.dependencies(
        inputParam(
            "0_0",
            "group",
            resolver.resolve(listOfMapOfStringToString()),
            Optional.empty(),
            new HashSet<>(),
            DocumentationType.SWAGGER_2,
            alternateTypeProvider(),
            namingStrategy,
            emptySet()))

    when:
    def mapped = Mappers.getMapper(ModelMapper)
        .mapModels(modelMap.values().stream()
            .collect(Collectors.toMap(getIds,
                Function.identity())))


    then:
    mapped.containsKey("java.util.Map<java.lang.String,java.lang.String>")
    and:
    def mappedModel = mapped.get("java.util.Map<java.lang.String,java.lang.String>")
    mappedModel.name == "MapOfstringAndstring"
    mappedModel.additionalProperties instanceof StringProperty
  }

  def "model dependencies are inferred correctly for list of ModelMap"() {
    given:
    Map<ResolvedType, Model> modelMap = modelProvider.dependencies(
        inputParam(
            "0_0",
            "group",
            resolver.resolve(listOfModelMap()),
            Optional.empty(),
            new HashSet<>(),
            DocumentationType.SWAGGER_2,
            alternateTypeProvider(),
            namingStrategy,
            emptySet()))

    when:
    def mapped = Mappers.getMapper(ModelMapper).mapModels(modelMap.values().stream()
        .collect(Collectors.toMap(getIds,
            Function.identity())))

    then:
    mapped.containsKey("org.springframework.ui.ModelMap")
    and:
    def mappedModel = mapped.get("org.springframework.ui.ModelMap")
    mappedModel.name == "ModelMap"
    mappedModel.additionalProperties instanceof ObjectProperty

  }

  def "model dependencies are inferred correctly for list of map of string to Simpletype"() {
    given:
    Map<ResolvedType, Model> modelMap = modelProvider.dependencies(
        inputParam(
            "0_0",
            "group",
            resolver.resolve(listOfMapOfStringToSimpleType()),
            Optional.empty(),
            new HashSet<>(),
            DocumentationType.SWAGGER_2,
            alternateTypeProvider(),
            namingStrategy,
            emptySet()))

    when:
    def mapped = Mappers.getMapper(ModelMapper).mapModels(modelMap.values().stream()
        .collect(Collectors.toMap(getIds,
            Function.identity())))

    then:
    mapped.containsKey("java.util.Map<java.lang.String,springfox.documentation.schema.SimpleType>")
    mapped.containsKey("springfox.documentation.schema.SimpleType")
    and:
    def mappedModel = mapped.get("java.util.Map<java.lang.String,springfox.documentation.schema.SimpleType>")
    mappedModel.name == "MapOfstringAndSimpleType"
    mappedModel.additionalProperties instanceof RefProperty

  }

  def "model dependencies are inferred correctly for list of erased map"() {
    given:
    Map<ResolvedType, Model> modelMap = modelProvider.dependencies(
        inputParam(
            "0_0",
            "group",
            resolver.resolve(listOfErasedMap()),
            Optional.empty(),
            new HashSet<>(),
            DocumentationType.SWAGGER_2,
            alternateTypeProvider(),
            namingStrategy,
            emptySet()))

    when:
    def mapped = Mappers.getMapper(ModelMapper).mapModels(modelMap)

    then:
    mapped.size() == 0
  }

  def "when the source models contain a property that has a generic type with one of the type bindings as Void"() {
    given:
    Model model = modelProvider.modelFor(
        inputParam(
            "0_0",
            "group",
            resolver.resolve(genericClassOfType(Void)),
            Optional.empty(),
            new HashSet<>(),
            DocumentationType.SWAGGER_2,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).get()
    def modelMap = new HashMap<>()

    and:
    modelMap.put("test", model)

    when:
    def mapped = Mappers.getMapper(ModelMapper).mapModels(modelMap)

    then:
    mapped.containsKey("test")

    and:
    def mappedModel = mapped.get("test")
    mappedModel.properties.size() == (model.properties.size() - 1)
    !mappedModel.properties.containsKey("genericField")
  }

  def "when the source models map is null"() {
    given:
    def modelMap = null

    when:
    def mapped = Mappers.getMapper(ModelMapper).mapModels(modelMap)

    then:
    mapped == null
  }

  def "models with allowable ranges are serialized correctly"() {
    given:
    Model model = modelProvider.modelFor(
        inputParam(
            "0_0",
            "group",
            resolver.resolve(simpleType()),
            Optional.empty(),
            new HashSet<>(),
            DocumentationType.SWAGGER_2,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).get()
    def modelMap = new HashMap<>()

    and:
    modelMap.put("test", model)

    and: "we add a fake allowable range"
    def intObject = model.properties.get("anObjectInt")
    def properties = new HashMap(model.properties)
    properties.put("anObjectInt", updatedIntObject(intObject))
    model = new ModelBuilder(model).properties(properties).build()
    modelMap.put("test", model)

    when:
    def mapped = Mappers.getMapper(ModelMapper).mapModels(modelMap)

    then:
    mapped.containsKey("test")

    and: "Required values contains the modified property"
    def mappedModel = mapped.get("test")
    mappedModel.properties.size() == model.properties.size()
    mappedModel.getRequired().size() == 1
    mappedModel.getRequired().contains("anObjectInt")

    and: "Range expectations are mapped correctly"
    def mappedIntObject = mappedModel.properties.get("anObjectInt")
    ((AbstractNumericProperty) mappedIntObject).minimum == 1
    ((AbstractNumericProperty) mappedIntObject).maximum == 2000
  }

  def "Properties that are Map subclasses that close closed generic types are supported"() {
    given:
    def model = Mock(Model)

    and:
    model.type >> customMapOfType(SimpleType)

    when:
    def valueClass = Mappers.getMapper(ModelMapper).typeOfValue(model)

    then:
    valueClass.isPresent()
    valueClass.get() == SimpleType
  }

  def "Properties that are Map subclasses that close the open generic types are supported"() {
    given:
    def model = Mock(Model)

    and:
    model.type >> customMapOpen()

    when:
    def valueClass = Mappers.getMapper(ModelMapper).typeOfValue(model)

    then:
    valueClass.isPresent()
    valueClass.get() == Object
  }

  def "Properties that are not maps will have the value class absent"() {
    given:
    def model = Mock(Model)

    and:
    model.type >> genericClassWithTypeErased()

    when:
    def valueClass = Mappers.getMapper(ModelMapper).typeOfValue(model)

    then:
    !valueClass.isPresent()
  }

  @Unroll
  def "safe parses #stringValue"() {
    when:
    def safeParsed = safeInteger(stringValue)

    then:
    safeParsed == expected

    where:
    stringValue | expected
    "0"         | 0
    "infinity"  | null
    "-infinity" | null
    "1.0"       | null

  }


  ModelProperty updatedIntObject(ModelProperty modelProperty) {
    ModelPropertyBuilder builder = new ModelPropertyBuilder()
    def newModel = builder
        .allowableValues(new AllowableRangeValues("1", "2000"))
        .description(modelProperty.description)
        .isHidden(modelProperty.hidden)
        .required(true)
        .name(modelProperty.name)
        .position(modelProperty.position)
        .type(modelProperty.type)
        .example(modelProperty.example)
        .xml(modelProperty.xml)
        .build()
    newModel.updateModelRef(createFactory(modelProperty))
  }


  def "models with allowable ranges are serialized correctly for string property"() {
    given:
    Model model = modelProvider.modelFor(
        inputParam(
            "0_0",
            "group",
            resolver.resolve(simpleType()),
            Optional.empty(),
            new HashSet<>(),
            DocumentationType.SWAGGER_2,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).get()
    def modelMap = new HashMap<>()

    and:
    modelMap.put("test", model)

    and: "we add a fake allowable range"
    def stringObject = model.properties.get("aString")
    def properties = new HashMap(model.properties)
    properties.put("aString", updatedStringObject(stringObject))
    model = new ModelBuilder(model).properties(properties).build()
    modelMap.put("test", model)

    when:
    def mapped = Mappers.getMapper(ModelMapper).mapModels(modelMap)

    then:
    mapped.containsKey("test")

    and: "Required values contains the modified property"
    def mappedModel = mapped.get("test")
    mappedModel.properties.size() == model.properties.size()
    mappedModel.getRequired().size() == 1
    mappedModel.getRequired().contains("aString")

    and: "Range expectations are mapped correctly"
    def mappedStringObject = mappedModel.properties.get("aString")
    ((StringProperty) mappedStringObject).minLength == 1
    ((StringProperty) mappedStringObject).maxLength == 255
  }

  ModelProperty updatedStringObject(ModelProperty modelProperty) {
    ModelPropertyBuilder builder = new ModelPropertyBuilder()
    def newModel = builder
        .allowableValues(new AllowableRangeValues("1", "255"))
        .description(modelProperty.description)
        .isHidden(modelProperty.hidden)
        .required(true)
        .name(modelProperty.name)
        .position(modelProperty.position)
        .type(modelProperty.type)
        .build()
    newModel.updateModelRef(createFactory(modelProperty))
  }

  def "model property positions affect the serialization order"() {
    given:
    def properties = [
        'a': createModelPropertyWithPosition('a', 3),
        'b': createModelPropertyWithPosition('b', 2),
        'c': createModelPropertyWithPosition('c', 1),
        'd': createModelPropertyWithPosition('d', 0),
        'e': createModelPropertyWithPosition('e', 4),
    ]
    Model model = createModel(properties)

    when:
    def mapped = Mappers.getMapper(ModelMapper).mapModels([test: model])

    then:
    mapped != null
    ['d', 'c', 'b', 'a', 'e'] == mapped['test'].properties.keySet().toList()
  }

  def "model property positions affect the serialization order with same positions"() {
    given:
    def properties = [
        'a': createModelPropertyWithPosition('a', 0),
        'b': createModelPropertyWithPosition('b', 0),
        'c': createModelPropertyWithPosition('c', 0),
        'd': createModelPropertyWithPosition('d', 0),
        'e': createModelPropertyWithPosition('e', 0),
    ]
    Model model = createModel(properties)

    when:
    def mapped = Mappers.getMapper(ModelMapper).mapModels([test: model])

    then:
    mapped != null
    ['a', 'b', 'c', 'd', 'e'] == mapped['test'].properties.keySet().toList()
  }

  ModelProperty createModelPropertyWithPosition(String name, int position) {
    def stringProperty = resolver.resolve(String)
    new ModelProperty(
        name,
        resolver.resolve(stringProperty),
        simpleQualifiedTypeName(stringProperty),
        position,
        false,
        false,
        false,
        false,
        '',
        null,
        '',
        '',
        '',
        null,
        []
    ).with {
      it.updateModelRef({ rt -> new ModelRef(simpleQualifiedTypeName(stringProperty)) })
      it
    }
  }

  Model createModel(properties) {
    def modelType = typeForTestingPropertyPositions()
    def model = new Model(
        'test',
        'test',
        modelType,
        simpleQualifiedTypeName(modelType),
        properties,
        '',
        '',
        '',
        null,
        '',
        null)
    model
  }

  Function createFactory(ModelProperty modelProperty) {
    new Function<ResolvedObjectType, ModelReference>() {
      @Override
      ModelReference apply(ResolvedObjectType type) {
        return modelProperty.getModelRef()
      }
    }
  }
}
