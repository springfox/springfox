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
package springfox.documentation.swagger.csrf

import org.junit.runner.RunWith
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import spock.lang.FailsWith
import spock.lang.Specification
import springfox.documentation.swagger.common.ClassUtils

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Sputnik.class)
@PrepareForTest([ClassUtils.class])
class CsrfTokenAccesserSpec extends Specification {

    class FakeCsrfToken {

        def getToken() {
            return ""
        }
    }

    class ExceptionalCsrfToken {
        def getToken() {
            throw new RuntimeException("They forced me!")
        }
    }

    def "When the csrfTokenClass is null"() {
        given:
        def accesser = new CsrfTokenAccesser("")

        expect:
        !accesser.accessible()
        accesser.access(new Object()) == null
    }

    def "When the csrfTokenClass contains none `getToken` method"() {
        given:
        def accesser = new CsrfTokenAccesser("java.lang.Object")

        expect:
        !accesser.accessible()
        accesser.access(new Object()) == null
    }

    @FailsWith(IllegalArgumentException.class)
    def "When the accesser tries to access a different object"() {
        given:
        def accesser = new CsrfTokenAccesser(FakeCsrfToken.class.getName())

        expect:
        accesser.accessible()
        accesser.access(new Object())
    }

    def "When the accessed object throws an exception"() {
        given:
        def accesser = new CsrfTokenAccesser(ExceptionalCsrfToken.class.getName())

        expect:
        accesser.accessible()
        accesser.access(new ExceptionalCsrfToken()) == null
    }
}
