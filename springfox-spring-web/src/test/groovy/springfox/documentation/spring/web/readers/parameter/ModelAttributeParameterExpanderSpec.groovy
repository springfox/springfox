/*
 *
 *  Copyright 2015-2018 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License")
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

package springfox.documentation.spring.web.readers.parameter

import com.fasterxml.classmate.TypeResolver
import org.joda.time.LocalDateTime
import springfox.documentation.schema.JacksonEnumTypeDeterminer
import springfox.documentation.schema.property.bean.AccessorsProvider
import springfox.documentation.schema.property.field.FieldProvider
import springfox.documentation.spi.schema.EnumTypeDeterminer
import springfox.documentation.spring.web.dummy.models.Example
import springfox.documentation.spring.web.dummy.models.ModelAttributeComplexTypeExample
import springfox.documentation.spring.web.dummy.models.ModelAttributeExample
import springfox.documentation.spring.web.dummy.models.SomeType
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

import java.beans.BeanInfo
import java.beans.IntrospectionException

import static springfox.documentation.schema.AlternateTypeRules.*

class ModelAttributeParameterExpanderSpec extends DocumentationContextSpec {
  TypeResolver typeResolver
  EnumTypeDeterminer enumTypeDeterminer
  ModelAttributeParameterExpander sut

  def setup() {
    typeResolver = new TypeResolver()
    enumTypeDeterminer = new JacksonEnumTypeDeterminer()
    plugin.alternateTypeRules(newRule(typeResolver.resolve(LocalDateTime), typeResolver.resolve(String)))
    sut = new ModelAttributeParameterExpander(
        new FieldProvider(typeResolver),
        new AccessorsProvider(typeResolver),
        enumTypeDeterminer)
    sut.pluginsManager = defaultWebPlugins()
  }

  def "should expand parameters"() {
    when:
    def parameters = sut.expand(new ExpansionContext("", typeResolver.resolve(Example), context()))

    then:
    parameters.size() == 10
    parameters.find { it.name == 'parentBeanProperty' }
    parameters.find { it.name == 'foo' }
    parameters.find { it.name == 'bar' }
    parameters.find { it.name == 'readOnlyString' }
    parameters.find { it.name == 'enumType' }
    parameters.find { it.name == 'annotatedEnumType' }
    parameters.find { it.name == 'propertyWithNoSetterMethod' }
    parameters.find { it.name == 'allCapsSet' }
    parameters.find { it.name == 'nestedType.name' }
    parameters.find { it.name == 'localDateTime' }
  }

  def "should expand lists and nested types"() {
    when:
    def parameters = sut.expand(new ExpansionContext("", typeResolver.resolve(ModelAttributeExample), context()))

    then:
    parameters.size() == 6
    parameters.find { it.name == 'stringProp' }
    parameters.find { it.name == 'intProp' }
    parameters.find { it.name == 'listProp' }
    parameters.find { it.name == 'arrayProp' }
    parameters.find { it.name == 'complexProp.name' }
    parameters.find { it.name == 'accountTypes' }
  }

  def "should expand complex types"() {
    when:
    def parameters = sut.expand(new ExpansionContext("", typeResolver.resolve(ModelAttributeComplexTypeExample), context()))

    then:
    parameters.size() == 12
    parameters.find { it.name == 'stringProp' }
    parameters.find { it.name == 'intProp' }
    parameters.find { it.name == 'listProp' }
    parameters.find { it.name == 'arrayProp' }
    parameters.find { it.name == 'complexProp.name' }
    parameters.find { it.name == 'fancyPets[0].categories[0].name' }
    parameters.find { it.name == 'fancyPets[0].id' }
    parameters.find { it.name == 'fancyPets[0].name' }
    parameters.find { it.name == 'fancyPets[0].age' }
    parameters.find { it.name == 'categories[0].name' }
    parameters.find { it.name == 'modelAttributeProperty' }
    parameters.find { it.name == 'accountTypes' }
  }

  def "should expand parameters when parent name is not empty"() {
    when:
    def parameters = sut.expand(new ExpansionContext("parent", typeResolver.resolve(Example), context()))

    then:
    parameters.size() == 10
    parameters.find { it.name == 'parent.parentBeanProperty' }
    parameters.find { it.name == 'parent.foo' }
    parameters.find { it.name == 'parent.bar' }
    parameters.find { it.name == 'parent.enumType' }
    parameters.find { it.name == 'parent.annotatedEnumType' }
    parameters.find { it.name == 'parent.propertyWithNoSetterMethod' }
    parameters.find { it.name == 'parent.allCapsSet' }
    parameters.find { it.name == 'parent.nestedType.name' }
    parameters.find { it.name == 'parent.localDateTime' }
  }

  def "should not expand causing stack overflow"() {
    when:
    def parameters = sut.expand(new ExpansionContext("parent", typeResolver.resolve(SomeType), context()))

    then:
    parameters.size() == 3
    parameters.find { it.name == 'parent.string1' }
    parameters.find { it.name == 'parent.otherType.string2' }
    parameters.find { it.name == 'parent.otherType.parent.string1' }
  }

  def "Should return empty set when there is an exception"() {
    given:
    ModelAttributeParameterExpander expander =
        new ModelAttributeParameterExpander(
            new FieldProvider(typeResolver),
            new AccessorsProvider(typeResolver),
            enumTypeDeterminer) {
          @Override
          BeanInfo getBeanInfo(Class<?> clazz) throws IntrospectionException {
            throw new IntrospectionException("Fail")
          }
        }
    expander.pluginsManager = defaultWebPlugins()

    when:
    def parameters = expander.expand(new ExpansionContext("", typeResolver.resolve(Example), context()))

    then:
    parameters.size() == 0
  }


  def "should handle expansion of Book"() {
    given:
    def parameters = sut.expand(
        new ExpansionContext(
            "",
            typeResolver.resolve(Book),
            context()))

    expect:
    parameters.size() == 3
    parameters.find { it.name == 'id' }
    parameters.find { it.name == 'authors[0].id' }
    parameters.find { it.name == 'authors[0].books[0].id' }
  }


  def "should handle expansion item with public fields"() {
    given:
    def parameters = sut.expand(
        new ExpansionContext(
            "",
            typeResolver.resolve(Bug2423),
            context()))

    expect:
    parameters.size() == 2
    parameters.find { it.name == 'from' }
    parameters.find { it.name == 'to' }
  }

  class Bug2423 {
    public String from
    public String to
  }

  class Book {
    private Long id
    private Set<Author> authors

    Long getId() {
      return id
    }

    void setId(Long id) {
      this.id = id
    }

    Set<Author> getAuthors() {
      return authors
    }

    void setAuthors(Set<Author> authors) {
      this.authors = authors
    }
  }

  class Author {
    private Long id
    private List<Book> books

    Long getId() {
      return id
    }

    void setId(Long id) {
      this.id = id
    }

    List<Book> getBooks() {
      return books
    }

    void setBooks(List<Book> books) {
      this.books = books
    }
  }

  def "should handle expansion of User"() {
    given:
    def parameters = sut.expand(
        new ExpansionContext(
            "",
            typeResolver.resolve(User),
            context()))

    expect:
    parameters.size() == 2
    parameters.find { it.name == 'office.parent.name' }
    parameters.find { it.name == 'office.name' }
  }

  class User {
    Office office

    Office getOffice() {
      return office
    }

    void setOffice(Office office) {
      this.office = office
    }
  }

  class Office extends TreeEntity<Office> {
    String name

    String getName() {
      return name
    }

    void setName(String name) {
      this.name = name
    }
  }

  class TreeEntity<T> {
    T  parent
    User user

    User getUser() {
      return user
    }

    void setUser(User user) {
      this.user = user
    }

    T getParent() {
      return parent
    }

    void setParent(T parent) {
      this.parent = parent
    }
  }
}
