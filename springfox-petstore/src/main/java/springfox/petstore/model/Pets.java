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

package springfox.petstore.model;

import java.util.Objects;
import java.util.function.Predicate;

public class Pets {
  private Pets() {
    throw new UnsupportedOperationException();
  }

  public static Predicate<Pet> statusIs(final String status) {
    return new Predicate<Pet>() {
      @Override
      public boolean test(Pet input) {
        return Objects.equals(input.getStatus(), status);
      }
    };
  }

  public static Predicate<Pet> tagsContain(final String tag) {
    return new Predicate<Pet>() {
      @Override
      public boolean test(Pet input) {
        return input.getTags().stream().anyMatch(withName(tag));
      }
    };
  }

  private static Predicate<Tag> withName(final String tag) {
    return new Predicate<Tag>() {
      @Override
      public boolean test(Tag input) {
        return Objects.equals(input.getName(), tag);
      }
    };
  }
}
