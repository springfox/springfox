/*
 *
 *  Copyright 2017-2019 the original author or authors.
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
package springfox.documentation.swagger.web

import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import org.springframework.mock.env.MockEnvironment
import spock.lang.Shared
import spock.lang.Specification
import springfox.documentation.builders.DocumentationBuilder
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.ResourceListing
import springfox.documentation.spring.web.DocumentationCache
import springfox.documentation.spring.web.csrf.ClassUtils
import springfox.documentation.spring.web.csrf.CsrfStrategy

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Sputnik.class)
@PrepareForTest([ClassUtils.class])
class ApiResourceControllerCsrfSpec extends Specification {

    protected static final TOKEN = "9d38280b-f58a-4dbd-9054-c118b0577622"

    class FakeCsrfToken {
        @SuppressWarnings("unused")
        def getToken() {
            return TOKEN
        }
    }

    static ENDPOINT = "/swagger-resources/csrf"

    static csrfToken = """{
    "token": "${TOKEN}",
    "parameterName": "_csrf",
    "headerName": "X-CSRF-TOKEN"
}"""

    static emptyCsrfToken = """{
    "token": "",
    "parameterName": "",
    "headerName": ""
}"""

    @Shared
    ApiResourceController apiResourceController
    @Shared
    CsrfStrategy strategy

    @SuppressWarnings(["GroovyAccessibility", "GroovyAssignabilityCheck"])
    def setupSpec() {
        apiResourceController = new ApiResourceController(
                new InMemorySwaggerResourcesProvider(
                        new MockEnvironment().with {
                            it.withProperty("springfox.documentation.swagger.v1.path", "/v1")
                            it.withProperty("springfox.documentation.swagger.v2.path", "/v2")
                            it
                        },
                        new DocumentationCache().with {
                            it.addDocumentation(new DocumentationBuilder()
                                    .name("test")
                                    .basePath("/base")
                                    .resourceListing(new ResourceListing("1.0", [], [], ApiInfo.DEFAULT))
                                    .build())
                            it
                        }).with {
                    swagger1Available = true
                    swagger2Available = true
                    it
                })

    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def powerMock() {
        PowerMockito.mockStatic(ClassUtils.class)
        Mockito.when(ClassUtils.forName("org.springframework.security.web.server.csrf.CsrfToken"))
                .thenReturn(FakeCsrfToken.class)
        Mockito.when(ClassUtils.forName("org.springframework.security.web.csrf.CsrfToken"))
                .thenReturn(FakeCsrfToken.class)
    }

    @SuppressWarnings("GroovyAccessibility")
    def using(CsrfStrategy strategy) {
        this.strategy = strategy
        def builder = UiConfigurationBuilder.builder()
        if (CsrfStrategy.NONE == strategy) {
            builder.disableCsrf()
        } else {
            builder.csrfStrategy(strategy)
        }
        apiResourceController.with {
            uiConfiguration = builder.build()
            it
        }
    }

}
