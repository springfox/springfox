package springfox.documentation.schema

import java.util.List;
import java.util.Map;

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver;

import spock.lang.Shared
import spock.lang.Specification;
import springfox.documentation.service.AllowableRangeValues

class ModelSpec extends Specification {
  static args = ["Ts", new TypeResolver().resolve(String), "qType1", 0, true, true,
                 true, "desc", new AllowableRangeValues("1","5"), "example", "pattern",
                 new ArrayList<>()]
      
  def "Model .equalsIgnoringName works as expected"() {
    given:
      ModelProperty property = Spy(ModelProperty, constructorArgs: args)
    
      def TypeResolver resolver = new TypeResolver()
      
      Model model = new Model("Test", "Test", resolver.resolve(String), "qType", ["Ts": property],
                              "desc", "bModel", "discr", ["subType1", "subType2"], "exmpl")

      Model testModel = new Model("Test", "Test", resolver.resolve(type), qualifiedType, props,
                                  description, baseModel, discriminator, subTypes, example)
    and:
      property.getModelRef() >> new ModelRef("string", null, true)
    expect:
      model.equalsIgnoringName(testModel) == expectedEquality
      model.equalsIgnoringName(model)
      !model.equalsIgnoringName(null)
      !model.equalsIgnoringName(new Object())
    where:
      type << [String, Integer, String, String, String, String, String, String, String]
      qualifiedType << ["qType", "qType", "qType1", "qType", "qType", "qType", "qType", "qType", "qType"]
      props << {
          ModelProperty pr = Spy(ModelProperty, constructorArgs: args)
          pr.getModelRef() >> new ModelRef("string", null, true)
          [["Ts": pr], ["T": pr], ["T": pr], ["T": pr], ["Ts": pr], ["Ts": pr], ["Ts": pr], ["Ts": pr], ["Ts": pr]]
      }()
      description << ["desc", "desc", "desc", "desc", "desc1", "desc", "desc", "desc", "desc"]
      baseModel << ["bModel", "bModel", "bModel", "bModel", "bModel", "bModel1", "bModel", "bModel", "bModel"]
      discriminator << ["discr", "discr", "discr", "discr", "discr", "discr", "discr1", "discr", "discr"]
      subTypes << [["subType1", "subType2"], [], [], [], [], [], [], [], ["subType1", "subType2"]]
      example << ["exmpl", "exmpl", "exmpl", "exmpl", "exmpl", "exmpl", "exmpl", "exmpl", "exmpl1"]
      expectedEquality << [true, false, false, false, false, false, false, false, false]
  }
}
