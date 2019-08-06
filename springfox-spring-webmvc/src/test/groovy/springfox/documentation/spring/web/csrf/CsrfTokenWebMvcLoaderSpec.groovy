package springfox.documentation.spring.web.csrf

import groovy.json.JsonOutput
import org.skyscreamer.jsonassert.JSONAssert
import spock.lang.Specification

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest

class CsrfTokenWebMvcLoaderSpec extends Specification {

    static final CsrfStrategy strategy = CsrfStrategy.COOKIE
    HttpServletRequest request

    def setup() {
        request = Mock(HttpServletRequest)
    }

    def "When cookie's value is empty and accesser is incapable"() {
        given:
        request.getCookies() >> [new Cookie(strategy.keyName, "")]
        def loader = new CsrfTokenWebMvcLoader(new DefaultCsrfTokenAccesser("")).wrap(request)

        expect:
        JSONAssert.assertEquals(
                JsonOutput.toJson(loader.loadFromCookie(strategy)),
                JsonOutput.toJson(MirrorCsrfToken.EMPTY), true)
    }
}
