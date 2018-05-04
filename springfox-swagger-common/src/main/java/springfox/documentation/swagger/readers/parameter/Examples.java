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


import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import io.swagger.annotations.ExampleProperty;
import springfox.documentation.schema.Example;

import java.util.Optional;
import java.util.function.Predicate;

import static org.springframework.util.StringUtils.isEmpty;

public class Examples {
  private Examples() {
    throw new UnsupportedOperationException();
  }

  public static Multimap<String, Example> examples(io.swagger.annotations.Example example) {
    Multimap<String, Example> examples = LinkedListMultimap.create();
    for (ExampleProperty each: example.value()) {
      if (!isEmpty(each.value())) {
        examples.put(each.mediaType(), new Example(Optional.ofNullable(each.mediaType())
                .filter(((Predicate<String>)String::isEmpty).negate()).orElse(null), each.value()));
      }
    }
    return examples;
  }
}
