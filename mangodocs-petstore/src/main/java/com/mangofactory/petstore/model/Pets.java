package com.mangofactory.petstore.model;


import com.google.common.base.Objects;
import com.google.common.base.Predicate;

import static com.google.common.collect.Iterables.*;

public class Pets {
  public static Predicate<Pet> statusIs(final String status) {
    return new Predicate<Pet>() {
      @Override
      public boolean apply(Pet input) {
        return Objects.equal(input.getStatus(), status);
      }
    };  
  }
  
  public static Predicate<Pet> tagsContain(final String tag) {
    return new Predicate<Pet>() {
      @Override
      public boolean apply(Pet input) {
        return any(input.getTags(), withName(tag));
      }
    };
  }

  private static Predicate<Tag> withName(final String tag) {
    return new Predicate<Tag>() {
      @Override
      public boolean apply(Tag input) {
        return Objects.equal(input.getName(), tag);
      }
    };
  }
}
