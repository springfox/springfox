package com.mangofactory.documentation.schema;

import com.google.common.base.Optional;

public class ModelRef {
  private final String type;
  private final Optional<String> itemType;

  public ModelRef(String type, String itemType) {
    this.type = type;
    this.itemType = Optional.fromNullable(itemType);
  }

  public ModelRef(String type) {
    this(type, null);
  }

  public String getType() {
    return type;
  }
  
  public boolean isCollection() {
    return itemType.isPresent();
  }
  
  public String getItemType() {
    return itemType.orNull();
  }
}
