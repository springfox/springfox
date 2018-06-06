package springfox.documentation.swagger1.web

import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

class HostNameProviderSpec extends Specification {
    def "should identify prefixing or replacing with x-forwarded-prefix"() {
        when:
        def result = HostNameProvider.isPreservePath(springVersion)

        then:
        result == expected

        where:
        springVersion     | expected
        "3.10.20.RELEASE" | true
        "4.2.20.RELEASE"  | true
        "4.3.14.RELEASE"  | true
        "4.3.15.RELEASE"  | false
        "4.4.16.RELEASE"  | false
        "5.0.0.RELEASE"   | true
        "5.0.5.RELEASE"   | false
        "5.1.0.RELEASE"   | false
        "5.1.5.RELEASE"   | false
        "6.1.6.RELEASE"   | false
    }

    def "should prefix path with x-forwarded-prefix"() {
        given:
        def request = Mock(HttpServletRequest.class)
        request.getHeader(HostNameProvider.X_FORWARDED_PREFIX) >> "/prefix"

        when:
        def result = HostNameProvider.prependForwardedPrefix(request, "/basePath")

        then:
        result == "/prefix"
    }

    def "should not be allowed to create object from utility class"() {
        when:
        new HostNameProvider()

        then:
        thrown UnsupportedOperationException
    }
}
