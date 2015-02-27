package com.mangofactory.petstore.model;


import com.google.common.base.Objects;
import com.google.common.base.Predicate;

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
        return input.getTags().contains(tag);
      }
    };
  }
}
