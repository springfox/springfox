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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.google.common.collect.FluentIterable.*;
import static com.google.common.collect.Sets.*;
import static springfox.documentation.builders.BuilderDefaults.*;

public class Tags {
  private Tags() {
    throw new UnsupportedOperationException();
  }

  public static Set<Tag> toTags(Multimap<String, ApiListing> apiListings) {
    Iterable<ApiListing> allListings = Iterables.concat(nullToEmptyMultimap(apiListings).asMap().values());
    List<Tag> tags = from(allListings)
        .transformAndConcat(collectTags())
        .toList();
    TreeSet<Tag> tagSet = newTreeSet(byTagName());
    tagSet.addAll(tags);
    return tagSet;
  }

  public static Comparator<Tag> byTagName() {
    return new Comparator<Tag>() {
      @Override
      public int compare(Tag first, Tag second) {
        return first.getName().compareTo(second.getName());
      }
    };
  }

  static Function<String, Tag> toTag(final ApiListing listing) {
    return new Function<String, Tag>() {
      @Override
      public Tag apply(String input){
        return new Tag(input, listing.getDescription());
      }
    };
  }

  static Function<ApiListing, Iterable<Tag>> collectTags() {
    return new Function<ApiListing, Iterable<Tag>>() {
      @Override
      public Iterable<Tag> apply(ApiListing input) {
        return from(input.getTags()).filter(emptyTags()).transform(toTag(input)).toSet();
      }
    };
  }

  public static Predicate<String> emptyTags() {
    return new Predicate<String>() {
      @Override
      public boolean apply(String input) {
        return !Strings.isNullOrEmpty(input);
      }
    };
  }
}
