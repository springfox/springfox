package springfox.documentation.spring.web

import com.fasterxml.classmate.TypeResolver
import com.google.common.base.Optional
import org.springframework.web.method.HandlerMethod
import spock.lang.Specification
import springfox.documentation.spring.web.json.JacksonModuleRegistrar

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * Created by mtalbot on 17/05/15.
 */
class HandlerMethodReturnTypesSpec extends Specification {

    static TypeResolver resolver = new TypeResolver();
    static def ListOfMapOfStringOfStringType = resolver.resolve(List.class, resolver.resolve(Map.class, String.class, String.class))
    static def StringType = resolver.resolve(String.class)

    def "Should return absent for type erased classes"() {
        expect:
            HandlerMethodReturnTypes.useType(input) == expected

        where:
        input                         || expected
        getProxy(test.class).class    || Optional.<Class>absent()
        Class.class                   || Optional.<Class>absent()
        String.class                  || Optional.<Class>of(String.class)
    }

    def "Should return the underlying type even if proxied"() {
        expect:
            HandlerMethodReturnTypes.handlerReturnType(resolver, input) == expected

        where:
        input                                                                             || expected
        new HandlerMethod(getProxy(test.class), test.class.getMethod("genericReturn"))    || ListOfMapOfStringOfStringType
        new HandlerMethod(getProxy(test.class), test.class.getMethod("simpleReturn"))     || StringType
        new HandlerMethod(new testy(), testy.class.getMethod("genericReturn"))            || ListOfMapOfStringOfStringType
        new HandlerMethod(new testy(), testy.class.getMethod("simpleReturn"))             || StringType
    }

    def getProxy(Class<?>... interfaces) {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), interfaces, new InvocationHandler() {
            @Override
            Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null
            }
        })
    }

    protected interface test {

        List<Map<String, String>> genericReturn()

        String simpleReturn()
    }

    protected class testy implements test {
        @Override
        List<Map<String, String>> genericReturn() {
            return null
        }

        @Override
        String simpleReturn() {
            return null
        }
    }
}
