/*
 *
 *  Copyright 2016 the original author or authors.
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
package springfox.documentation.schema.property

import spock.lang.Specification

class SimpleMethodSignatureEqualitySpec extends Specification {
  def "detects method equality on different classes"() {
    given:
      def sut = new SimpleMethodSignatureEquality()
      def m1 = SwaggerBugRequest.methods.find {
        m -> "setInvisibleField".equals(m.name) &&
            1 == m.parameterTypes.length &&
            String.class.equals(m.parameterTypes[0])}
      def m2 = SwaggerBugIF.methods.find { m -> "setInvisibleField".equals(m.name)  }
    expect:
      sut.test(m1, m2)
  }

  def "detects method inequality on different classes"() {
    given:
      def sut = new SimpleMethodSignatureEquality()
      def m1 = SwaggerBugRequest.methods.find { m -> "setVisibleField".equals(m.name) }
      def m2 = SwaggerBugIF.methods.find { m -> "setInvisibleField".equals(m.name) }
    expect:
      !sut.test(m1, m2)
  }

  def "detects method inequality on different classes when parameter count differs"() {
    given:
      def sut = new SimpleMethodSignatureEquality()
      def m1 = SwaggerBugRequest.methods.find { m -> "setInvisibleField".equals(m.name) && 1 == m.parameterTypes.length }
      def m2 = SwaggerBugExtended.methods.find { m -> "setInvisibleField".equals(m.name) && 2 == m.parameterTypes.length }
    expect:
      !sut.test(m1, m2)
  }

  def "detects method inequality when parameter type differs"() {
    given:
    def sut = new SimpleMethodSignatureEquality()
    def m1 = SwaggerBugExtended.methods.find {
      m -> "setInvisibleField".equals(m.name) &&
          1 == m.parameterTypes.length &&
        String.class.equals(m.parameterTypes[0])}
    def m2 = SwaggerBugExtended.methods.find {
      m -> "setInvisibleField".equals(m.name) &&
          1 == m.parameterTypes.length &&
        Boolean.class.equals(m.parameterTypes[0])}
    expect:
      !sut.test(m1, m2)
  }

  def "Computes hashcode"() {
    given:
      def sut = new SimpleMethodSignatureEquality()
      def m1 = SwaggerBugRequest.methods.find {
        m -> "setInvisibleField".equals(m.name) &&
            1 == m.parameterTypes.length &&
            String.class.equals(m.parameterTypes[0])}
    expect:
      sut.doHash(m1) > 0
  }


  class SwaggerBugRequest extends SwaggerBugExtended implements SwaggerBugIF {
    private String visibleField;

    public String getVisibleField() {
      return visibleField;
    }

    public void setVisibleField(String visibleField) {
      this.visibleField = visibleField;
    }
  }

  class SwaggerBugExtended {
    private String invisibleField;

    public String getInvisibleField() {
      return invisibleField;
    }

    public void setInvisibleField(String invisibleField) {
      this.invisibleField = invisibleField;
    }

    public void setInvisibleField(Boolean another) {
    }

    public void setInvisibleField(String invisibleField, boolean another) {
      this.invisibleField = invisibleField;
    }
  }

  interface SwaggerBugIF {

    String getVisibleField();

    void setVisibleField(String visibleField);

    String getInvisibleField();

    void setInvisibleField(String invisibleField);

  }
}
