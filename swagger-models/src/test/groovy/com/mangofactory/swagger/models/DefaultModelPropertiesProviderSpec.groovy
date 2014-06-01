package com.mangofactory.swagger.models

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import spock.lang.Specification

@Mixin(TypesForTestingSupport)
class DefaultModelPropertiesProviderSpec extends Specification {
  def "Property names are identified correctly based on (get/set) method names"() {
    given:
      def propertyProvider = new AccessorsProvider(new TypeResolver())

    expect:
      propertyProvider.propertyName(methodName) == property

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
      def accessors = new AccessorsProvider(new TypeResolver())

    expect:
      accessors.isGetter(method) == isGetter

    where:
      methodName      || isGetter
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
      def accessors = new AccessorsProvider(new TypeResolver())

    expect:
      accessors.isSetter(method) == isGetter

    where:
      methodName      || isGetter
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
