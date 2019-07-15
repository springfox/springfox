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
import springfox.documentation.swagger.common.ClassUtils
import springfox.documentation.swagger.csrf.CsrfStrategy

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Sputnik.class)
@PrepareForTest([ClassUtils.class])
class ApiResourceControllerCsrfSpec extends Specification {

    static final TOKEN = "9d38280b-f58a-4dbd-9054-c118b0577622"

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

        PowerMockito.mockStatic(ClassUtils.class)
        Mockito.when(ClassUtils.forName("org.springframework.security.web.server.csrf.CsrfToken"))
                .thenReturn(FakeCsrfToken.class)
        Mockito.when(ClassUtils.forName("org.springframework.security.web.csrf.CsrfToken"))
                .thenReturn(FakeCsrfToken.class)

    }

    @SuppressWarnings("GroovyAccessibility")
    <T> T derive(CsrfStrategy strategy, Closure<T> cl) {
        cl(apiResourceController.with {
            uiConfiguration = UiConfigurationBuilder.builder()
                    .csrfStrategy(strategy)
                    .build()
            it
        })
    }

}
