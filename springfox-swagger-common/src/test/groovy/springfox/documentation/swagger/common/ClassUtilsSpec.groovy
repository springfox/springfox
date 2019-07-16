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
package springfox.documentation.swagger.common

import org.junit.runner.RunWith
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
