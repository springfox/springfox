package springfox.documentation.swagger2.web

import com.fasterxml.classmate.TypeResolver
import org.springframework.http.server.RequestPath
import org.springframework.http.server.reactive.ServerHttpRequest
import spock.lang.Unroll
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.Defaults
import springfox.documentation.spring.web.DocumentationCache
import springfox.documentation.spring.web.json.JsonSerializer
import springfox.documentation.spring.web.mixins.ApiListingSupport
import springfox.documentation.spring.web.mixins.AuthSupport
import springfox.documentation.spring.web.mixins.JsonSupport
import springfox.documentation.spring.web.paths.DefaultPathProvider
import springfox.documentation.spring.web.plugins.DefaultConfiguration
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.scanners.ApiDocumentationScanner
import springfox.documentation.spring.web.scanners.ApiListingReferenceScanResult
import springfox.documentation.spring.web.scanners.ApiListingReferenceScanner
import springfox.documentation.spring.web.scanners.ApiListingScanner
import springfox.documentation.swagger2.configuration.Swagger2JacksonModule
import springfox.documentation.swagger2.mappers.MapperSupport

import static springfox.documentation.spi.service.contexts.Orderings.nickNameComparator

@Mixin([ApiListingSupport, AuthSupport])
class Swagger2ControllerWebFluxSpec extends DocumentationContextSpec
    implements MapperSupport, JsonSupport {

    Swagger2ControllerWebFlux controller = new Swagger2ControllerWebFlux(
            new DocumentationCache(),
            swagger2Mapper(),
            new JsonSerializer([new Swagger2JacksonModule()])
    )

    ApiListingReferenceScanner listingReferenceScanner
    ApiListingScanner listingScanner
    ServerHttpRequest request;

    def setup() {
        listingReferenceScanner = Mock(ApiListingReferenceScanner)
        listingReferenceScanner.scan(_) >> new ApiListingReferenceScanResult(new HashMap<>())
        listingScanner = Mock(ApiListingScanner)
        listingScanner.scan(_) >> new HashMap<>()
    }

    @Unroll("x-forwarded-prefix: #prefix")
    def "should respect proxy headers ('X-Forwarded-*') when setting host, port and basePath"() {
        given:
        def req = serverRequestWithXHeaders(prefix)

        def defaultConfiguration = new DefaultConfiguration(
                new Defaults(),
                new TypeResolver(),
                new DefaultPathProvider())

        this.contextBuilder = defaultConfiguration.create(DocumentationType.SWAGGER_12)
                .requestHandlers([])
                .operationOrdering(nickNameComparator())

        ApiDocumentationScanner swaggerApiResourceListing =
                new ApiDocumentationScanner(listingReferenceScanner, listingScanner)
        controller.documentationCache.addDocumentation(swaggerApiResourceListing.scan(documentationContext()))

        when:
        def result = jsonBodyResponse(controller.getDocumentation(null, req).getBody().value())

        then:
        result.basePath == expectedPath

        where:
        prefix        | expectedPath
        "/fooservice" | "/fooservice"
        "/"           | "/"
        ""            | "/"
    }

    def serverRequestWithXHeaders(String prefix) {

        ServerHttpRequest request = Mock()
        request.path >> RequestPath.parse(new URI("http://localhost${-> prefix}/api"), prefix)
        request.URI >> URI.create("http://localhost/api-docs")

        request
    }
}
