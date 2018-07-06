/*
 *
 *  Copyright 2015-2019 the original author or authors.
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
package springfox.documentation.swagger2.mappers

import io.swagger.models.auth.ApiKeyAuthDefinition
import io.swagger.models.auth.In
import spock.lang.Specification
import springfox.documentation.service.*

class ApiKeyAuthFactorySpec extends Specification {

  def "Create ApiKey scheme definition"() {
    given:
      SecurityScheme security = new ApiKey("mykey", "key1", "header", amazonVendorExtensions())
    and:
      ApiKeyAuthFactory factory = new ApiKeyAuthFactory()
    when:
      def securityDefinition = factory.create(security)
    then:
      securityDefinition.type == "apiKey"
      ((ApiKeyAuthDefinition)securityDefinition).getName() == "key1"
      ((ApiKeyAuthDefinition)securityDefinition).getIn() == In.HEADER
      ((ApiKeyAuthDefinition)securityDefinition).vendorExtensions == expectedExtensions()
  }

  def amazonVendorExtensions(
      String authorizerKey = 'x-amazon-apigateway-authorizer',
      String authTypeKey = 'x-amazon-apigateway-authtype') {

    def authorizer = new ObjectVendorExtension(authorizerKey)
    def arns = new ListVendorExtension<String>(
        "providerARNS",
        ["arn:aws:cognito-idp:{region}:{account_id}:userpool/{user_pool_id}"])
    authorizer.with {
      addProperty(new StringVendorExtension("type", "cognito_user_pools"))
      addProperty(arns)
    }

    def authType = new StringVendorExtension(authTypeKey, 'cognito_user_pools')
    def vendorExtensions = [authType, authorizer]
    vendorExtensions
  }

  def expectedExtensions() {
    [
        "x-amazon-apigateway-authtype"  : "cognito_user_pools",
        "x-amazon-apigateway-authorizer": [
            "type"        : "cognito_user_pools",
            "providerARNS": [
                "arn:aws:cognito-idp:{region}:{account_id}:userpool/{user_pool_id}"
            ]
        ]
    ]
  }
}