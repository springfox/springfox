/*
 *
 *  Copyright 2015 the original author or authors.
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

package springfox.documentation.schema;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Nikolai Iusiumbeli
 * date: 12/08/2017
 */
public class XmlTest {
  @Test
  public void equalsHashCodeContractTest() throws Exception {
    Xml xml1 = new Xml().name("1").attribute(true).namespace("2").prefix("3").wrapped(true);
    Xml xml2 = new Xml().name("1").attribute(true).namespace("2").prefix("3").wrapped(true);
    assertThat(xml1, is(xml2));
    EqualsVerifier.forClass(Xml.class)
        .suppress(Warning.NONFINAL_FIELDS)
        .usingGetClass()
        .verify();
  }
}