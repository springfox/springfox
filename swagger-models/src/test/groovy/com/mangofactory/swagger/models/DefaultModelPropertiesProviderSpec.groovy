package com.mangofactory.swagger.models

import com.mangofactory.swagger.mixins.TypesForTestingSupport
import spock.lang.Specification

import static com.mangofactory.swagger.models.property.bean.Accessors.isGetter
import static com.mangofactory.swagger.models.property.bean.Accessors.isSetter
import static com.mangofactory.swagger.models.property.bean.Accessors.propertyName

@Mixin(TypesForTestingSupport)
class DefaultModelPropertiesProviderSpec extends Specification {
  def "Property names are identified correctly based on (get/set) method names"() {
    given:
    expect:
      propertyName(methodName) == property

    where:
      methodName  || property
      "getProp"   || "prop"
      "getProp1"  || "prop1"
      "getProp_1" || "prop_1"
      "isProp"    || "prop"
      "isProp1"   || "prop1"
      "isProp_1"  || "prop_1"
      "setProp"   || "prop"
      "setProp1"  || "prop1"
      "setProp_1" || "prop_1"
      "Prop"      || ""
      "Prop1"     || ""
      "Prop_1"    || ""
  }

  def "Getters are identified correctly"() {
    given:
      def sut = typeForTestingGettersAndSetters()
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
      def sut = typeForTestingGettersAndSetters()
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
