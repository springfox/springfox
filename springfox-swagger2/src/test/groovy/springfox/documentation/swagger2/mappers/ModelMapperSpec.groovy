package springfox.documentation.swagger2.mappers

import io.swagger.models.properties.AbstractNumericProperty
import io.swagger.models.properties.ObjectProperty
import io.swagger.models.properties.RefProperty
import io.swagger.models.properties.StringProperty
import org.mapstruct.factory.Mappers
import springfox.documentation.builders.ModelPropertyBuilder
import springfox.documentation.schema.*
import springfox.documentation.schema.mixins.TypesForTestingSupport
import springfox.documentation.service.AllowableRangeValues
import springfox.documentation.spi.DocumentationType

import static com.google.common.base.Functions.*
import static com.google.common.base.Suppliers.*
import static com.google.common.collect.Maps.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, AlternateTypesSupport])
class ModelMapperSpec extends SchemaSpecification {
  def namingStrategy = new CodeGenGenericTypeNamingStrategy()
  def "models are serialized correctly" (){
    given:
      Model model = modelProvider.modelFor(inputParam(typeToTest,
            DocumentationType.SWAGGER_2, alternateTypeProvider(), namingStrategy)).get()
      def modelMap = newHashMap()
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
      typeToTest  << [simpleType(), mapsContainer(), enumType(), typeWithLists()]
  }

  def "model dependences are inferred correctly for list of map of string to string" (){
    given:
      Map<String, Model> modelMap = modelProvider.dependencies(inputParam(listOfMapOfStringToString(),
        DocumentationType.SWAGGER_2, alternateTypeProvider(), namingStrategy))
    when:
      def mapped = Mappers.getMapper(ModelMapper).mapModels(modelMap)
    then:
      mapped.containsKey("MapOfstringAndstring")
    and:
      def mappedModel = mapped.get("MapOfstringAndstring")
      mappedModel.additionalProperties instanceof StringProperty
  }

  def "model dependencies are inferred correctly for list of ModelMap" (){
    given:
      Map<String, Model> modelMap = modelProvider.dependencies(inputParam(listOfModelMap(),
        DocumentationType.SWAGGER_2, alternateTypeProvider(), namingStrategy))
    when:
      def mapped = Mappers.getMapper(ModelMapper).mapModels(modelMap)
    then:
      mapped.containsKey("ModelMap")
    and:
      def mappedModel = mapped.get("ModelMap")
      mappedModel.additionalProperties instanceof ObjectProperty
  }

  def "model dependencies are inferred correctly for list of map of string to Simpletype" (){
    given:
      Map<String, Model> modelMap = modelProvider.dependencies(inputParam(listOfMapOfStringToSimpleType(),
        DocumentationType.SWAGGER_2, alternateTypeProvider(), namingStrategy))
    when:
      def mapped = Mappers.getMapper(ModelMapper).mapModels(modelMap)
    then:
      mapped.containsKey("MapOfstringAndSimpleType")
      mapped.containsKey("SimpleType")
    and:
      def mappedModel = mapped.get("MapOfstringAndSimpleType")
      mappedModel.additionalProperties instanceof RefProperty
  }

  def "model dependencies are inferred correctly for list of erased map" (){
    given:
      Map<String, Model> modelMap = modelProvider.dependencies(inputParam(listOfErasedMap(),
        DocumentationType.SWAGGER_2, alternateTypeProvider(), namingStrategy))
    when:
      def mapped = Mappers.getMapper(ModelMapper).mapModels(modelMap)
    then:
      mapped.size() == 0
  }

  def "when the source models contain a property that has a generic type with one of the type bindings as Void" (){
    given:
      Model model = modelProvider.modelFor(inputParam(genericClassOfType(Void),
          DocumentationType.SWAGGER_2, alternateTypeProvider(), namingStrategy)).get()
      def modelMap = newHashMap()
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

  def "when the source models map is null" (){
    given:
      def modelMap = null
    when:
      def mapped = Mappers.getMapper(ModelMapper).mapModels(modelMap)
    then:
      mapped == null
  }

  def "models with allowable ranges are serialized correctly" (){
    given:
      Model model = modelProvider.modelFor(inputParam(simpleType(),
          DocumentationType.SWAGGER_2, alternateTypeProvider(), namingStrategy)).get()
      def modelMap = newHashMap()
    and:
      modelMap.put("test", model)
    and: "we add a fake allowable range"
      def intObject = model.properties.get("anObjectInt")
      model.properties.put("anObjectInt", updatedIntObject(intObject))
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
      ((AbstractNumericProperty)mappedIntObject).minimum == 1
      ((AbstractNumericProperty)mappedIntObject).maximum == 2000
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
      .build()
    newModel.updateModelRef(forSupplier(ofInstance((modelProperty.modelRef))))
  }

}
