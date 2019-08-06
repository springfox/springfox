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
package springfox.documentation.spring.web.csrf

import spock.lang.FailsWith
import spock.lang.Specification

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

    def "When the csrfTokenClass could not be found"() {
        given:
        def accesser = new DefaultCsrfTokenAccesser("")

        expect:
        !accesser.available()
        accesser.access(new Object()) == null
    }

    def "When the csrfTokenClass contains none `getToken` method"() {
        given:
        def accesser = new DefaultCsrfTokenAccesser("java.lang.Object")

        expect:
        !accesser.available()
        accesser.access(new Object()) == null
    }

    @FailsWith(IllegalArgumentException.class)
    def "When the accesser tries to access a different object"() {
        given:
        def accesser = new DefaultCsrfTokenAccesser(FakeCsrfToken.class.getName())

        expect:
        accesser.available()
        accesser.access(new Object())
    }

    def "When the accessed object throws an exception"() {
        given:
        def accesser = new DefaultCsrfTokenAccesser(ExceptionalCsrfToken.class.getName())

        expect:
        accesser.available()
        accesser.access(new ExceptionalCsrfToken()) == null
    }
}
