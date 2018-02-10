package springfox.documentation.schema

import com.fasterxml.classmate.TypeResolver
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.service.AllowableRangeValues
import springfox.documentation.service.VendorExtension

class ModelSpec extends Specification {
  @Unroll
  def "Model .equalsIgnoringName works as expected"() {
    given:
    def resolver = new TypeResolver()

    Model model = new Model(
        "Test",
        "Test",
        resolver.resolve(String),
        "qType",
        ["Ts": prop("Ts")],
        "desc",
        "bModel",
        "discr",
        ["subType1", "subType2"],
        "exmpl",
        new Xml())

    Model testModel = new Model(
        "Test",
        "Test",
        resolver.resolve(type),
        qualifiedType,
        props,
        description,
        baseModel,
        discriminator,
        subTypes,
        example,
        new Xml())

    expect:
    model.equalsIgnoringName(testModel) == expectedEquality
    model.equalsIgnoringName(model)
    !model.equalsIgnoringName(null)
    !model.equalsIgnoringName(new Object())

    where:
    type    | qualifiedType | description | baseModel | discriminator | example  | subTypes                 | props          | expectedEquality
    String  | "qType"       | "desc"      | "bModel"  | "discr"       | "exmpl"  | ["subType1", "subType2"] | ["Ts": prop("Ts")] | true
    Integer | "qType1"      | "desc"      | "bModel"  | "discr"       | "exmpl"  | []                       | ["T": prop("T")]  | false
    String  | "qType"       | "desc"      | "bModel"  | "discr"       | "exmpl"  | []                       | ["T": prop("T")]  | false
    String  | "qType"       | "desc"      | "bModel"  | "discr"       | "exmpl"  | []                       | ["T": prop("T")]  | false
    String  | "qType"       | "desc1"     | "bModel"  | "discr"       | "exmpl"  | []                       | ["Ts": prop("Ts")] | false
    String  | "qType"       | "desc"      | "bModel1" | "discr"       | "exmpl"  | []                       | ["Ts": prop("Ts")] | false
    String  | "qType"       | "desc"      | "bModel"  | "discr1"      | "exmpl"  | []                       | ["Ts": prop("Ts")] | false
    String  | "qType"       | "desc"      | "bModel"  | "discr"       | "exmpl"  | []                       | ["Ts": prop("Ts")] | false
    String  | "qType"       | "desc"      | "bModel"  | "discr"       | "exmpl1" | ["subType1", "subType2"] | ["Ts": prop("Ts")] | false
  }

  def prop(String name) {
    new ModelProperty(name,
        new TypeResolver().resolve(String),
        "qType1",
        0,
        true,
        true,
        true,
        false,
        "desc",
        new AllowableRangeValues("1", "5"),
        "example",
        "pattern",
        "default",
        new Xml(),
        new ArrayList<VendorExtension>())
  }
}
