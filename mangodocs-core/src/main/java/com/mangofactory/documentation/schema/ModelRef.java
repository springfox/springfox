package com.mangofactory.documentation.schema;

import com.google.common.base.Optional;

public class ModelRef {
  private final String type;
  private final boolean isMap;
  private final Optional<String> itemType;

  public ModelRef(String type, String itemType) {
    this(type, itemType, false);
  }

  public ModelRef(String type, String itemType, boolean isMap) {
    this.type = type;
    this.isMap = isMap;
    this.itemType = Optional.fromNullable(itemType);
  }

  public ModelRef(String type) {
    this(type, null);
  }

  public String getType() {
    return type;
  }
  
  public boolean isCollection() {
    return itemType.isPresent() && !isMap;
  }

  public boolean isMap() {
    return itemType.isPresent() && isMap;
  }

  public String getItemType() {
    return itemType.orNull();
  }
}
