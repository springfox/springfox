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
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Shared
import springfox.documentation.spring.web.csrf.ClassUtils
import springfox.documentation.spring.web.csrf.CsrfStrategy
import springfox.documentation.spring.web.csrf.CsrfWebMvcConfigurer

import javax.servlet.http.Cookie

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content

class ApiResourceControllerCsrfWebMvcSpec extends ApiResourceControllerCsrfSpec {

    @Shared
    MockMvc mvc
    @Shared
    Closure<MockHttpServletRequestBuilder> edit = {
        MockHttpServletRequestBuilder builder -> builder
    }

    def givenSessionAttribute() {
        edit = { builder ->
            builder.sessionAttr("org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository.CSRF_TOKEN",
                    new FakeCsrfToken())
        }
    }

    def givenAttribute() {
        edit = { builder ->
            builder.requestAttr(strategy.parameterName, new FakeCsrfToken())
        }
    }

    def expecting(String json) {
        mvc.perform(edit(get(ENDPOINT)
                .accept(MediaType.APPLICATION_JSON)))
                .andExpect(content().json(json))
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    @Override
    def setupSpec() {
        setup()

        def configurer = new CsrfWebMvcConfigurer()
        configurer.registerDefaultLoader()
        def resolvers = []
        configurer.addArgumentResolvers(resolvers)

        mvc = MockMvcBuilders.standaloneSetup(apiResourceController)
                .setCustomArgumentResolvers(*resolvers).build()
    }

    def setup() {
        powerMock()
        Mockito.when(ClassUtils.isMvc()).thenReturn(true)
        Mockito.when(ClassUtils.isFlux()).thenReturn(false)
    }

    def cleanup() {
        edit = {
            MockHttpServletRequestBuilder builder -> builder
        }
    }

    @SuppressWarnings("GroovyAccessibility")
    def "WebMvc - csrf token not supported"() {
        given:
        using(CsrfStrategy.NONE)

        expect:
        expecting(emptyCsrfToken)
    }

    def "WebMvc - csrf tokens stored in session"() {
        given:
        using(CsrfStrategy.SESSION)
        givenSessionAttribute()

        expect:
        expecting(csrfToken)
    }

    def "WebMvc - csrf tokens not stored in session yet, but been temporarily stashed in request"() {
        given:
        using(CsrfStrategy.SESSION)
        givenAttribute()

        expect:
        expecting(csrfToken)
    }

    def "WebMvc - csrf tokens stored in cookie"() {
        given:
        using(CsrfStrategy.COOKIE)
        edit = { builder ->
            builder.cookie(new Cookie(strategy.keyName, TOKEN))
        }

        expect:
        expecting(csrfToken)
    }

    def "WebMvc - csrf tokens not stored in cookie yet, but been temporarily stashed in request"() {
        given:
        using(CsrfStrategy.COOKIE)
        givenAttribute()

        expect:
        expecting(csrfToken)
    }

    def "WebMvc - csrf is configured to be stored in cookie, but none is stored anywhere"() {
        given:
        using(CsrfStrategy.COOKIE)

        expect:
        expecting(emptyCsrfToken)
    }

    def "WebMvc - csrf is configured to be stored in session, but none is stored anywhere"() {
        given:
        using(CsrfStrategy.SESSION)

        expect:
        expecting(emptyCsrfToken)
    }

    def "WebMvc - cors requests should get an empty csrf token"() {
        given:
        using(CsrfStrategy.SESSION)
        edit = { builder ->
            builder.requestAttr(strategy.parameterName, new FakeCsrfToken())
                    .header("Origin", "http://foreign.origin.com")
        }

        expect:
        expecting(emptyCsrfToken)
    }
}
