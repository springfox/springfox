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
package springfox.documentation.swagger.readers.parameter;


import io.swagger.annotations.ExampleProperty;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import springfox.documentation.builders.ExampleBuilder;
import springfox.documentation.schema.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.springframework.util.StringUtils.*;

public class Examples {
  private Examples() {
    throw new UnsupportedOperationException();
  }

  public static Map<String, List<Example>> examples(io.swagger.annotations.Example example) {
    Map<String, List<Example>> examples = new HashMap<>();
    for (ExampleProperty each : example.value()) {
      if (!isEmpty(each.value())) {
        examples.putIfAbsent(each.mediaType(), new LinkedList<>());
        examples.get(each.mediaType()).add(new ExampleBuilder()
            .mediaType(each.mediaType())
            .value(each.value())
            .build());
      }
    }
    return examples;
  }

  public static List<Example> allExamples(io.swagger.annotations.Example example) {
    List<Example> examples = new ArrayList<>();
    for (ExampleProperty each : example.value()) {
      if (!isEmpty(each.value())) {
        examples.add(new ExampleBuilder()
            .mediaType(each.mediaType())
            .value(each.value())
            .build());
      }
    }
    return examples;
  }

  public static Map<String, List<Example>> examples(String mediaType, ExampleObject[] exampleObjects) {
    Map<String, List<Example>> examples = new HashMap<>();
    for (ExampleObject each : exampleObjects) {
      if (!isEmpty(each.value())) {
        examples.putIfAbsent(mediaType, new LinkedList<>());
        examples.get(mediaType).add(new ExampleBuilder()
                                               .mediaType(mediaType)
                                               .value(each.value())
                                               .build());
      }
    }
    return examples;
  }

  public static List<Example> allExamples(String mediaType, ExampleObject[] exampleObjects) {
    List<Example> examples = new ArrayList<>();
    for (ExampleObject each : exampleObjects) {
      if (!isEmpty(each.value())) {
        examples.add(new ExampleBuilder()
                         .mediaType(mediaType)
                         .value(each.value())
                         .build());
      }
    }
    return examples;
  }
}
