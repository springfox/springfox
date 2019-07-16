package springfox.documentation.swagger.common

import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import spock.lang.Specification

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Sputnik.class)
@PrepareForTest([ClassUtils.class])
class ClassUtilsSpec extends Specification {

    def "Should forName capture ClassNotFoundException"() {
        given:
        PowerMockito.stub(PowerMockito.method(
                Class.class, "forName", String.class)).toThrow(new ClassNotFoundException())

        expect:
        ClassUtils.forName("java.lang.Object") == null
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "Should isFlux return accordingly"() {
        given:
        PowerMockito.stub(PowerMockito.method(
                ClassUtils.class, "forName", String.class)).toReturn(clz)

        expect:
        ClassUtils.isFlux() == isFlux

        where:
        clz            | isFlux
        null           | false
        Object.class   | true
    }


    @SuppressWarnings("GroovyAssignabilityCheck")
    def "Should isMvc return accordingly"() {
        given:
        PowerMockito.stub(PowerMockito.method(
                ClassUtils.class, "forName", String.class)).toReturn(clz)

        expect:
        ClassUtils.isMvc() == isMvc

        where:
        clz            | isMvc
        null           | false
        Object.class   | true
    }
}
