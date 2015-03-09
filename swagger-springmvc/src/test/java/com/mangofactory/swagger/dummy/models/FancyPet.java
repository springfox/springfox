package com.mangofactory.swagger.dummy.models;


import java.util.List;

public class FancyPet extends Pet {
  private List<Category> categories;

  public List<Category> getCategories() {
    return categories;
  }

  public void setCategories(List<Category> categories) {
    this.categories = categories;
  }
}
