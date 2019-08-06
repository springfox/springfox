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

import org.mockito.Mockito
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import reactor.core.publisher.Mono
import spock.lang.Shared
import springfox.documentation.spring.web.csrf.ClassUtils
import springfox.documentation.spring.web.csrf.CsrfStrategy
import springfox.documentation.spring.web.csrf.CsrfWebFluxConfigurer


class ApiResourceControllerCsrfWebFluxSpec extends ApiResourceControllerCsrfSpec {

    class Bridge {
        Closure<Mono<?>> cl
    }

    @Shared
    Bridge bridge = new Bridge()
    @Shared
    WebTestClient flux

    def givenSessionAttribute() {
        bridge.with {
            cl = { ServerWebExchange e ->
                e.getSession().doOnNext({ s ->
                    s.getAttributes().put(
                            "org.springframework.security.web.server.csrf.WebSessionServerCsrfTokenRepository.CSRF_TOKEN",
                            new FakeCsrfToken())
                })
            }
        }
    }

    def givenAttribute() {
        bridge.with {
            cl = { ServerWebExchange e ->
                e.getAttributes().put(
                        "org.springframework.security.web.server.csrf.CsrfToken",
                        Mono.just(new FakeCsrfToken()))
                Mono.empty()
            }
        }
    }

    def expecting(json) {
        flux.get().accept(MediaType.APPLICATION_JSON)
                .uri(ENDPOINT)
                .exchange()
                .expectBody().json(json)
    }

    @Override
    def setupSpec() {
        setup()

        def configuer = new CsrfWebFluxConfigurer()
        configuer.registerDefaultLoader()

        flux = WebTestClient.bindToController(apiResourceController)
                .webFilter({ exchange, chain ->
                    Mono.justOrEmpty(bridge.cl)
                            .flatMap({ cl -> cl(exchange) })
                            .switchIfEmpty(Mono.empty())
                            .then(chain.filter(exchange))
                } as WebFilter)
                .argumentResolvers({ c -> configuer.configureArgumentResolvers(c) })
                .build()
    }

    def setup() {
        powerMock()
        Mockito.when(ClassUtils.isFlux()).thenReturn(true)
        Mockito.when(ClassUtils.isMvc()).thenReturn(false)
    }

    def cleanup() {
        bridge.with { cl = { x -> Mono.empty() } }
    }

    def "WebFlux - csrf token not supported"() {
        given:
        using(CsrfStrategy.NONE)

        expect:
        expecting(emptyCsrfToken)
    }

    def "WebFlux - csrf tokens stored in session"() {
        given:
        using(CsrfStrategy.SESSION)
        givenSessionAttribute()

        expect:
        expecting(csrfToken)
    }

    def "WebFlux - csrf tokens not stored in session yet, but been temporarily stashed in request"() {
        given:
        using(CsrfStrategy.SESSION)
        givenAttribute()

        expect:
        expecting(csrfToken)
    }

    def "WebFlux - csrf tokens stored in cookie"() {
        given:
        using(CsrfStrategy.COOKIE)

        expect:
        flux.get().cookie(strategy.keyName, TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .uri(ENDPOINT)
                .exchange()
                .expectBody().json(csrfToken)
    }

    def "WebFlux - csrf tokens not stored in cookie yet, but been temporarily stashed in request"() {
        given:
        using(CsrfStrategy.COOKIE)
        givenAttribute()

        expect:
        expecting(csrfToken)
    }

    def "WebFlux - csrf is configured to be stored in cookie, but none is stored anywhere"() {
        given:
        using(CsrfStrategy.COOKIE)

        expect:
        expecting(emptyCsrfToken)
    }

    def "WebFlux - csrf is configured to be stored in session, but none is stored anywhere"() {
        given:
        using(CsrfStrategy.SESSION)

        expect:
        expecting(emptyCsrfToken)
    }

    def "WebFlux - cors requests should get an empty csrf token"() {
        given:
        using(CsrfStrategy.SESSION)
        givenSessionAttribute()

        expect:
        flux.get().header("Origin", "http://foreign.origin.com")
                .accept(MediaType.APPLICATION_JSON)
                .uri("http://dummy/" + ENDPOINT)
                .exchange()
                .expectBody().json(emptyCsrfToken)
    }
}
