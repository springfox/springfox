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

package springfox.documentation.swagger2.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.DecimalProperty;
import io.swagger.models.properties.MapProperty;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class PropertyExampleSerializerTest {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final Swagger2JacksonModule swagger2JacksonModule = new Swagger2JacksonModule();

  @BeforeClass
  public static void setupClass() {
    swagger2JacksonModule.maybeRegisterModule(objectMapper);
  }

  @Test
  public void serializePropertyExampleBoolean() throws Exception {
    BooleanProperty booleanPropertyTrue = new BooleanProperty();
    booleanPropertyTrue.setExample("true");
    BooleanProperty booleanPropertyFalse = new BooleanProperty();
    booleanPropertyFalse.setExample("false");

    String serializedTrue = objectMapper.writeValueAsString(booleanPropertyTrue);
    Assert.assertTrue(serializedTrue.contains("\"example\":true"));
    String serializedFalse = objectMapper.writeValueAsString(booleanPropertyFalse);
    Assert.assertTrue(serializedFalse.contains("\"example\":false"));
  }

  @Test
  public void serializePropertyExampleNumber() throws Exception {
    DecimalProperty decimalProperty = new DecimalProperty();
    decimalProperty.setExample("-0.42");

    String serialized = objectMapper.writeValueAsString(decimalProperty);
    Assert.assertTrue(serialized.contains("\"example\":-0.42"));
  }

  @Test
  public void serializePropertyExampleArray() throws Exception {
    ArrayProperty arrayProperty = new ArrayProperty();
    arrayProperty.setExample("[42, \"foo\", true]");

    String serialized = objectMapper.writeValueAsString(arrayProperty);
    Assert.assertTrue(serialized.contains("[42, \"foo\", true]"));
  }

  @Test
  public void serializePropertyExampleObject() throws Exception {
    MapProperty mapProperty = new MapProperty();
    mapProperty.setExample("{\"0\": {\"name\": \"foo\", \"age\": 42}, \"1\": {\"name\": \"bar\", \"valid\": true}}");

    String serialized = objectMapper.writeValueAsString(mapProperty);
    Assert.assertTrue(serialized.contains(
            "\"example\":{\"0\": {\"name\": \"foo\", \"age\": 42}, \"1\": {\"name\": \"bar\", \"valid\": true}}"));
  }

}