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

package springfox.documentation.builders;

import springfox.documentation.service.Parameter;

import java.util.function.Function;
import java.util.function.Predicate;

public class Parameters {
  private Parameters() {
    throw new UnsupportedOperationException();
  }

  public static Predicate<Parameter> withName(final String name) {
    return new Predicate<Parameter>() {
      @Override
      public boolean test(Parameter input) {
        return name.equals(input.getName());
      }
    };
  }

  public static Function<Parameter, String> toParameterName() {
    return new Function<Parameter, String>() {
      @Override
      public String apply(Parameter input) {
        return input.getName();
      }
    };
  }
}
