package com.mangofactory.documentation.schema.property

import com.mangofactory.documentation.schema.TypeForTestingPropertyNames
import com.mangofactory.documentation.schema.TypeWithGettersAndSetters
import spock.lang.Specification

import static com.mangofactory.documentation.schema.property.bean.Accessors.*

class DefaultModelPropertiesProviderSpec extends Specification {
  def "Property names are identified correctly based on (get/set) method names"() {
    given:
      def sut = TypeForTestingPropertyNames
      def method = sut.methods.find { it.name.equals(methodName) }

    expect:
      propertyName(method) == property

    where:
      methodName       || property
      "getProp"        || "prop"
      "getProp1"       || "prop1"
      "getProp_1"      || "prop_1"
      "isProp"         || "prop"
      "isProp1"        || "prop1"
      "isProp_1"       || "prop_1"
      "setProp"        || "prop"
      "setProp1"       || "prop1"
      "setProp_1"      || "prop_1"
      "prop"           || ""
      "getAnotherProp" || "prop"
      "setAnotherProp" || "prop"
      "getPropFallback"|| "propFallback"
      "setPropFallback"|| "propFallback"
  }

  def "Getters are identified correctly"() {
    given:
      def sut = TypeWithGettersAndSetters
      def method = sut.methods.find { it.name.equals(methodName) }

    expect:
      isGetter(method) == result

    where:
      methodName      || result
      "getIntProp"    || true
      "setIntProp"    || false
      "isBoolProp"    || true
      "setBoolProp"   || false
      "getVoid"       || false
      "isNotGetter"   || false
      "getWithParam"  || false
      "setNotASetter" || false
  }

  def "Setters are identified correctly"() {
    given:
      def sut = TypeWithGettersAndSetters
      def method = sut.methods.find { it.name.equals(methodName) }

    expect:
      isSetter(method) == result

    where:
      methodName      || result
      "getIntProp"    || false
      "setIntProp"    || true
      "isBoolProp"    || false
      "setBoolProp"   || true
      "getVoid"       || false
      "isNotGetter"   || false
      "getWithParam"  || false
      "setNotASetter" || false
  }
}
