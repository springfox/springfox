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
package springfox.documentation.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.isEmpty;
import static springfox.documentation.builders.BuilderDefaults.*;

public class Tags {
  private Tags() {
    throw new UnsupportedOperationException();
  }

  public static Set<Tag> toTags(Map<String, List<ApiListing>> apiListings) {
    Iterable<ApiListing> allListings = nullToEmptyMultimap(apiListings).values().stream().flatMap(l -> l.stream()).collect(toList());
    List<Tag> tags =
        StreamSupport.stream(allListings.spliterator(), false)
            .map(collectTags()).flatMap(tagIterable -> StreamSupport.stream(tagIterable.spliterator(), false))
            .collect(toList());
    TreeSet<Tag> tagSet = new TreeSet(tagComparator());
    tagSet.addAll(tags);
    return tagSet;
  }

  public static Comparator<Tag> tagComparator() {
    return byOrder()
        .thenComparing(thenByName());
  }

  private static Comparator<Tag> thenByName() {
    return new Comparator<Tag>() {
      @Override
      public int compare(Tag first, Tag second) {
        return first.getName().compareTo(second.getName());
      }
    };
  }

  private static Comparator<Tag> byOrder() {
    return new Comparator<Tag>() {
      @Override
      public int compare(Tag first, Tag second) {
        return Integer.valueOf(first.getOrder()).compareTo(second.getOrder());
      }
    };
  }

  public static Function<String, Tag> toTag(final Function<String, String> descriptor) {
    return new Function<String, Tag>() {
      @Override
      public Tag apply(String input) {
        return new Tag(input, descriptor.apply(input));
      }
    };
  }

  public static Function<String, String> descriptor(
      final Map<String, Tag> tagLookup,
      final String defaultDescription) {

    return new Function<String, String>() {
      @Override
      public String apply(String input) {
        return ofNullable(tagLookup.get(input))
            .map(toTagDescription())
            .orElse(defaultDescription);
      }
    };
  }

  private static Function<Tag, String> toTagDescription() {
    return new Function<Tag, String>() {
      @Override
      public String apply(Tag input) {
        return input.getDescription();
      }
    };
  }

  public static Function<Tag, String> toTagName() {
    return new Function<Tag, String>() {
      @Override
      public String apply(Tag input) {
        return input.getName();
      }
    };
  }

  static Function<ApiListing, Iterable<Tag>> collectTags() {
    return new Function<ApiListing, Iterable<Tag>>() {
      @Override
      public Iterable<Tag> apply(ApiListing input) {
        return input.getTags();
      }
    };
  }

  public static Predicate<String> emptyTags() {
    return new Predicate<String>() {
      @Override
      public boolean test(String input) {
        return !isEmpty(input);
      }
    };
  }
}
