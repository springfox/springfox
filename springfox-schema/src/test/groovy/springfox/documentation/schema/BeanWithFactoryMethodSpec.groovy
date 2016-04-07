package springfox.documentation.schema
import springfox.documentation.schema.mixins.ModelProviderSupport
import springfox.documentation.schema.mixins.TypesForTestingSupport

import static springfox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, ModelProviderSupport, AlternateTypesSupport])
class BeanWithFactoryMethodSpec extends SchemaSpecification {
  def "Type with bean properties in the constructor" () {
    given:
    def sut = defaultModelProvider()
    def typeToTest = typeWithConstructorProperties()
    def reqContext = inputParam(typeToTest, documentationType, alternateTypeProvider(), new
        DefaultGenericTypeNamingStrategy())
    def resContext = returnValue(typeToTest, documentationType, alternateTypeProvider(), new
        DefaultGenericTypeNamingStrategy())

    when:
    def models = [sut.modelFor(reqContext).get(), sut.modelFor(resContext).get()]

    then:
    models.each {
      it.properties.size() == 2
      it.properties.containsKey(fieldName)
      it.properties."$fieldName".description == description
      it.properties."$fieldName".required == isRequired
      it.properties."$fieldName".type.erasedType == type
      it.properties."$fieldName".qualifiedType == qualifiedTypeName
      it.properties."$fieldName".allowableValues == allowableValues
      true
    }

    where:
    fieldName || description  | isRequired  | type    | qualifiedTypeName   | allowableValues
    "foo"     || null         | true        | String  | "java.lang.String"  | null
    "bar"     || null         | true        | Integer | "java.lang.Integer" | null
  }

  def "Type with delegated constructor (factory method)" () {
    given:
    def sut = defaultModelProvider()
    def typeToTest = typeWithDelegatedConstructor()
    def reqContext = inputParam(typeToTest, documentationType, alternateTypeProvider(), new
        DefaultGenericTypeNamingStrategy())
    def resContext = returnValue(typeToTest, documentationType, alternateTypeProvider(), new
        DefaultGenericTypeNamingStrategy())

    when:
    def models = [sut.modelFor(reqContext).get(), sut.modelFor(resContext).get()]

    then:
    models.each {
      it.properties.size() == 2
      it.properties.containsKey(fieldName)
      it.properties."$fieldName".description == description
      it.properties."$fieldName".required == isRequired
      it.properties."$fieldName".type.erasedType == type
      it.properties."$fieldName".qualifiedType == qualifiedTypeName
      it.properties."$fieldName".allowableValues == allowableValues
      true
    }

    where:
    fieldName || description  | isRequired  | type    | qualifiedTypeName   | allowableValues
    "foo"     || null         | true        | String  | "java.lang.String"  | null
    "bar"     || null         | true        | Integer | "java.lang.Integer" | null
  }

  def "Type with @JsonCreator marked constructor" () {
    given:
    def sut = defaultModelProvider()
    def typeToTest = typeWithDelegatedConstructor()
    def reqContext = inputParam(typeToTest, documentationType, alternateTypeProvider(), new
        DefaultGenericTypeNamingStrategy())
    def resContext = returnValue(typeToTest, documentationType, alternateTypeProvider(), new
        DefaultGenericTypeNamingStrategy())

    when:
    def models = [sut.modelFor(reqContext).get(), sut.modelFor(resContext).get()]

    then:
    models.each {
      it.properties.size() == 2
      it.properties.containsKey(fieldName)
      it.properties."$fieldName".description == description
      it.properties."$fieldName".required == isRequired
      it.properties."$fieldName".type.erasedType == type
      it.properties."$fieldName".qualifiedType == qualifiedTypeName
      it.properties."$fieldName".allowableValues == allowableValues
      true
    }

    where:
    fieldName || description  | isRequired  | type    | qualifiedTypeName   | allowableValues
    "foo"     || null         | true        | String  | "java.lang.String"  | null
    "bar"     || null         | true        | Integer | "java.lang.Integer" | null
  }
}

