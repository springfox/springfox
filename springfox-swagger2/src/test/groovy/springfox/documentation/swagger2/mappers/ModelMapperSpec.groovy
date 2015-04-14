package springfox.documentation.swagger2.mappers

import com.fasterxml.classmate.ResolvedType
import com.google.common.base.Function
import com.wordnik.swagger.models.properties.AbstractNumericProperty
import org.mapstruct.factory.Mappers
import springfox.documentation.builders.ModelPropertyBuilder
import springfox.documentation.schema.*
import springfox.documentation.schema.mixins.TypesForTestingSupport
import springfox.documentation.service.AllowableRangeValues
import springfox.documentation.spi.DocumentationType

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
    newModel.updateModelRef(copyExisting(modelProperty.modelRef))
  }

  Function<ResolvedType, ModelRef> copyExisting(ModelRef modelRef) {
    return new Function<ResolvedType, ModelRef>() {
      @Override
      ModelRef apply(ResolvedType input) {
        return modelRef;
      }
    }
  }
}
