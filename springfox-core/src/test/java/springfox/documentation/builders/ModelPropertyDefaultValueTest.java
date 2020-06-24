/*
 *
 *  Copyright 2015-2018 the original author or authors.
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

package springfox.documentation.builders;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("deprecation")
public class ModelPropertyDefaultValueTest {

    private ModelPropertyBuilder propertyBuilder;
    private springfox.documentation.schema.ModelProperty modelProperty;

    @Before
    public void createNewPropertyBuilder() {
        propertyBuilder = new ModelPropertyBuilder();
        modelProperty = null;
    }

    @Test
    public void testDefaultValueNull() {
        modelProperty = propertyBuilder.build();
        assertEquals(modelProperty.getDefaultValue(), null);
    }

    @Test
    public void testDefaultValueEmpty() {
        modelProperty = propertyBuilder.defaultValue("")
                                       .build();
        assertEquals(modelProperty.getDefaultValue(), "");
    }

    @Test
    public void testDefaultValueString() {
        String testValue = "TEST Default value";
        modelProperty = propertyBuilder.defaultValue(testValue)
                                       .build();
        assertEquals(modelProperty.getDefaultValue(), testValue);
    }

}
