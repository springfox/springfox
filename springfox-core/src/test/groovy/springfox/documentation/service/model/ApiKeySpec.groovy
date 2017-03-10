/*
 *
 *  Copyright 2015-2018 the original author or authors.
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

package springfox.documentation.service.model

import spock.lang.Specification
import springfox.documentation.service.ApiKey
import springfox.documentation.service.ListVendorExtension
import springfox.documentation.service.ObjectVendorExtension
import springfox.documentation.service.StringVendorExtension

class ApiKeySpec extends Specification {
  def "Bean properties are set as expected via constructor" () {
    when:
      def apiKey = new ApiKey("mykey", "key1", "header")
    then:
      apiKey.type == "apiKey"
    and:
      apiKey.keyname == "key1"
      apiKey.name == "mykey"
      apiKey.passAs == "header"
  }

  def "Bean properties are set as expected via constructor with vendor extensions" () {
    when:
      def apiKeyObject = new ApiKey("mykey", "key1", "header")

    def apiKey = new ApiKey("mykey", "key1", "header", amazonVendorExtensions())
    then:
      apiKey.type == "apiKey"
    and:
      apiKey.keyname == "key1"
      apiKey.name == "mykey"
      apiKey.passAs == "header"
      apiKey.vendorExtensions.size()  == 2
      apiKey.vendorExtensions[0].name == 'x-amazon-apigateway-authtype'
      apiKey.vendorExtensions[1].name == 'x-amazon-apigateway-authorizer'
  }

  def "Bean properties are set as expected via constructor without vendor extensions" () {
    when:
      def apiKey = new ApiKey("mykey", "key1", "header", null)
    then:
      apiKey.type == "apiKey"
    and:
      apiKey.keyname == "key1"
      apiKey.name == "mykey"
      apiKey.passAs == "header"
      apiKey.vendorExtensions.size()  == 0
  }

  def "Bean properties are set as expected via constructor with invalid vendor extensions" () {
    when:
      def apiKey = new ApiKey("mykey", "key1", "header", invalidAmazonVendorExtensions())
    then:
      apiKey.type == "apiKey"
    and:
      apiKey.keyname == "key1"
      apiKey.name == "mykey"
      apiKey.passAs == "header"
      apiKey.vendorExtensions.size()  == 0
  }

  def invalidAmazonVendorExtensions() {
    amazonVendorExtensions('amazon-apigateway-authorizer', 'amazon-apigateway-authtype')
  }

  def amazonVendorExtensions(
      String authorizerKey = 'x-amazon-apigateway-authorizer',
      String authTypeKey = 'x-amazon-apigateway-authtype') {
    /*
      "x-amazon-apigateway-authtype": "cognito_user_pools",
      "x-amazon-apigateway-authorizer": {
        "type": "cognito_user_pools",
        "providerARNs": [
            "arn:aws:cognito-idp:{region}:{account_id}:userpool/{user_pool_id}"
        ]
      }*/
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
}
