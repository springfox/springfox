package springfox.documentation.swagger.web

import org.mockito.Mockito
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import springfox.documentation.swagger.common.ClassUtils
import springfox.documentation.swagger.csrf.CsrfStrategy

import javax.servlet.http.Cookie

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content

class ApiResourceControllerCsrfWebMvcSpec extends ApiResourceControllerCsrfSpec {

    CsrfStrategy strategy
    MockMvc mvc

    void derive(CsrfStrategy strategy) {
        this.strategy = strategy
        mvc = derive(this.strategy) {
            MockMvcBuilders.standaloneSetup(
                    new ApiResourceController.CsrfWebMvcController(it))
                    .build()
        }
    }

    @Override
    def setupSpec() {
        Mockito.when(ClassUtils.isMvc()).thenReturn(true)
    }

    @SuppressWarnings("GroovyAccessibility")
    def "WebMvc - csrf token not supported"() {
        given:
        derive(CsrfStrategy.NONE)

        expect:
        mvc.perform(get(ENDPOINT).accept(MediaType.APPLICATION_JSON))
                .andExpect(content().json(emptyCsrfToken))
    }

    def "WebMvc - csrf tokens stored in session"() {
        given:
        derive(CsrfStrategy.SESSION)

        expect:
        mvc.perform(get(ENDPOINT)
                .sessionAttr("org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository.CSRF_TOKEN",
                        new FakeCsrfToken())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().json(csrfToken))
    }

    def "WebMvc - csrf tokens not stored in session yet, but been temporarily stashed in request"() {
        given:
        derive(CsrfStrategy.SESSION)

        expect:
        mvc.perform(get(ENDPOINT)
                .requestAttr(strategy.parameterName, new FakeCsrfToken())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().json(csrfToken))
    }

    def "WebMvc - csrf tokens stored in cookie"() {
        given:
        derive(CsrfStrategy.COOKIE)

        expect:
        mvc.perform(get(ENDPOINT)
                .cookie(new Cookie(strategy.keyName, TOKEN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().json(csrfToken))
    }

    def "WebMvc - csrf tokens not stored in cookie yet, but been temporarily stashed in request"() {
        given:
        derive(CsrfStrategy.COOKIE)

        expect:
        mvc.perform(get(ENDPOINT)
                .requestAttr(strategy.parameterName, new FakeCsrfToken())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().json(csrfToken))
    }

    def "WebMvc - cors requests should be prohibited"() {
        given:
        derive(CsrfStrategy.SESSION)

        expect:
        mvc.perform(get(ENDPOINT)
                .requestAttr(strategy.parameterName, new FakeCsrfToken())
                .header("Origin", "http://foreign.origin.com")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().json(emptyCsrfToken))
    }
}
