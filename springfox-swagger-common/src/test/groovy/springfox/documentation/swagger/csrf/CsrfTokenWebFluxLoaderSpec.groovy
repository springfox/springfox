package springfox.documentation.swagger.csrf

import groovy.json.JsonOutput
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.http.HttpCookie
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.util.MultiValueMap
import org.springframework.web.server.ServerWebExchange
import spock.lang.Specification

class CsrfTokenWebFluxLoaderSpec extends Specification {

    static final CsrfStrategy strategy = CsrfStrategy.COOKIE
    ServerWebExchange exchange
    def cookies

    def setup() {
        exchange = Mock(ServerWebExchange)

        def request = Mock(ServerHttpRequest)
        exchange.getRequest() >> request

        cookies = Mock(MultiValueMap)
        request.getCookies() >> cookies

    }

    def "When cookie's value is empty and accesser is incapable"() {
        given:
        cookies.getFirst(strategy.keyName) >>
                new HttpCookie(strategy.keyName, "")
        def loader = new CsrfTokenWebFluxLoader(new DefaultCsrfTokenAccesser("")).wrap(exchange)

        expect:
        loader.loadFromCookie(strategy).doOnNext({
            t ->
                JSONAssert.assertEquals(JsonOutput.toJson(t),
                        JsonOutput.toJson(MirrorCsrfToken.EMPTY), true)
        })
    }
}
