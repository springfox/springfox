/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import static java.util.Optional.*;
import static java.util.stream.Collectors.*;
import static org.springframework.util.StringUtils.*;
import static springfox.documentation.builders.BuilderDefaults.*;

public class Tags {
  private Tags() {
    throw new UnsupportedOperationException();
  }

  public static Set<Tag> toTags(Map<String, List<ApiListing>> apiListings) {
    Iterable<ApiListing> allListings = nullToEmptyMultimap(apiListings).values().stream()
        .flatMap(Collection::stream)
        .collect(toList());
    List<Tag> tags =
        StreamSupport.stream(allListings.spliterator(), false)
            .map((Function<ApiListing, Iterable<Tag>>) ApiListing::getTags)
            .flatMap(tagIterable -> StreamSupport.stream(tagIterable.spliterator(), false))
            .collect(toList());
    TreeSet<Tag> tagSet = new TreeSet<>(tagComparator());
    tagSet.addAll(tags);
    return tagSet;
  }

  public static Comparator<Tag> tagComparator() {
    return byOrder()
        .thenComparing(thenByName());
  }

  private static Comparator<Tag> thenByName() {
    return Comparator.comparing(Tag::getName);
  }

  private static Comparator<Tag> byOrder() {
    return Comparator.comparingInt(Tag::getOrder);
  }

  public static Function<String, Tag> toTag(final Function<String, String> descriptor) {
    return input -> new Tag(input, descriptor.apply(input));
  }

  public static Function<String, String> descriptor(
      final Map<String, Tag> tagLookup,
      final String defaultDescription) {

    return input -> ofNullable(tagLookup.get(input))
        .map(Tag::getDescription)
        .orElse(defaultDescription);
  }

  public static Predicate<String> emptyTags() {
    return input -> !isEmpty(input);
  }
}
