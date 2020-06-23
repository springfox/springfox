package springfox.documentation.spring.web.readers.parameter

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.classmate.members.ResolvedField
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.builders.RequestParameterBuilder
import springfox.documentation.schema.ExampleEnum
import springfox.documentation.schema.JacksonEnumTypeDeterminer
import springfox.documentation.schema.property.field.FieldProvider
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.ParameterExpansionContext

class ExpandedParameterBuilderSpec extends Specification {
  def "List of enums are expanded correctly"() {
    given:
    ExpandedParameterBuilder sut = new ExpandedParameterBuilder(
        new TypeResolver(),
        new JacksonEnumTypeDeterminer()
    )

    and:
    ParameterExpansionContext context = new ParameterExpansionContext(
        "Test",
        "",
        "",
        new ModelAttributeParameterMetadataAccessor(
            [named("enums").rawMember],
            named("enums").type,
            "enums"),
        DocumentationType.SWAGGER_12,
        new ParameterBuilder(),
        new RequestParameterBuilder())

    when:
    sut.apply(context)
    def param = context.parameterBuilder.build()

    then:
    param.name == "enums"
    param.modelRef.type == "List"
    param.modelRef.itemModel().isPresent()

    and:
    def itemModel = param.modelRef.itemModel().get()
    itemModel.type == "string"
    itemModel.getAllowableValues().values.size() == 2
  }

  @Unroll
  def "List of scalar type for #field is expanded correctly"() {
    given:
    ExpandedParameterBuilder sut = new ExpandedParameterBuilder(
        new TypeResolver(),
        new JacksonEnumTypeDeterminer()
    )

    and:
    ParameterExpansionContext context = new ParameterExpansionContext(
        "Test",
        "",
        "",
        new ModelAttributeParameterMetadataAccessor(
            [named(field).rawMember],
            named(field).type,
            field),
        DocumentationType.SWAGGER_12,
        new ParameterBuilder(),
        new RequestParameterBuilder())

    when:
    sut.apply(context)
    def param = context.parameterBuilder.build()

    then:
    param.name == field
    param.modelRef.type == paramType
    param.modelRef.itemModel().isPresent()

    and:
    def itemModel = param.modelRef.itemModel().get()
    itemModel.type == itemType

    where:
    field           | paramType | itemType
    "enums"         | "List"    | "string"
    "strings"       | "List"    | "string"
    "integers"      | "List"    | "int"
    "uuids"         | "List"    | "uuid"
    "enumsArray"    | "Array"   | "string"
    "stringsArray"  | "Array"   | "string"
    "integersArray" | "Array"   | "int"
    "uuidsArray"    | "Array"   | "uuid"
  }

  def named(String name) {
    def resolver = new TypeResolver()
    FieldProvider fieldProvider = new FieldProvider(resolver)
    for (ResolvedField field : fieldProvider.in(resolver.resolve(A))) {
      if (field.name == name) {
        return field
      }
    }
  }

  class A {
    public List<ExampleEnum> enums;
    public List<String> strings;
    public List<Integer> integers;
    public List<UUID> uuids;
    public ExampleEnum[] enumsArray;
    public String[] stringsArray;
    public Integer[] integersArray;
    public UUID[] uuidsArray;
  }
}
