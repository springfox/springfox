/*
 *
 *  Copyright 2015-2020 the original author or authors.
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

package springfox.documentation.swagger2.util;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class UserInputLogEncoderTest {
    @Test
    public void testUserInputShouldBeSanitized() throws Exception {
        String encodedValid = UserInputLogEncoder.urlEncode("V2");
        assertTrue(encodedValid.equals("V2"));

        String encodedLogInject = UserInputLogEncoder.urlEncode("V2\ninjected-line");
        assertTrue(encodedLogInject.equals("V2%0Ainjected-line"));

        String encodedHtmlJsInject = UserInputLogEncoder.urlEncode("V2<script>alert('hi')</script>");
        assertTrue(encodedHtmlJsInject.equals("V2%3Cscript%3Ealert%28%27hi%27%29%3C%2Fscript%3E"));
    }
}
