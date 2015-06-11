/*
 *
 *  Copyright 2015 the original author or authors.
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

package springfox.test.contract.swaggertests
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ImplicitGrantBuilder
import springfox.documentation.builders.OAuthBuilder
import springfox.documentation.service.*

import static com.google.common.collect.Lists.*

@Configuration
public class SecuritySupport {
  @Bean
  SecurityScheme oauth() {
    new OAuthBuilder()
            .name("petstore_auth")
            .grantTypes(grantTypes())
            .scopes(scopes())
            .build()
  }

  @Bean
  SecurityScheme apiKey() {
    new ApiKey("api_key", "api_key", "header")
  }

  List<AuthorizationScope> scopes() {
    newArrayList(
      new AuthorizationScope("write:pets", "modify pets in your account"),
      new AuthorizationScope("read:pets", "read your pets"))
  }

  List<GrantType> grantTypes() {
    newArrayList(new ImplicitGrantBuilder()
            .loginEndpoint(new LoginEndpoint("http://petstore.swagger.io/api/oauth/dialog"))
            .build())
  }
}