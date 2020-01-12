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
package springfox.documentation.swagger.readers.operation

import springfox.documentation.service.ObjectVendorExtension
import springfox.documentation.service.StringVendorExtension
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

class VendorExtensionsReaderSpec extends DocumentationContextSpec implements RequestMappingSupport {
  def "should read from annotations"() {
    given:
    OperationContext operationContext =
        operationContext(documentationContext(), dummyHandlerMethod('methodWithExtensions'))
      VendorExtensionsReader sut = new VendorExtensionsReader()
    when:
      sut.apply(operationContext)
      def operation = operationContext.operationBuilder().build()
    and:
      !sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
    then:
      operation.vendorExtensions.size() == 2
      operation.vendorExtensions.first().equals(first())
      operation.vendorExtensions.subList(1, 2).first().equals(second())
  }

  def second() {
    def second = new ObjectVendorExtension("x-test2")
    second.with {
      addProperty(new StringVendorExtension("name2", "value2"))
    }
    second
  }

  def first() {
    def first = new ObjectVendorExtension("")
    first.with {
      addProperty(new StringVendorExtension("x-test1", "value1"))
    }
    first
  }
}
