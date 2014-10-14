package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Optional;
import com.mangofactory.swagger.models.property.ModelProperty;
import com.wordnik.swagger.models.properties.ArrayProperty;
import com.wordnik.swagger.models.properties.Property;
import com.wordnik.swagger.models.properties.RefProperty;

import static com.mangofactory.swagger.models.Collections.*;
import static com.mangofactory.swagger.models.ResolvedTypes.*;
import static com.mangofactory.swagger.models.SimpleProperties.*;

public class ArrayProperties {
  public static Optional<? extends Property> from(ModelProperty property) {
    ResolvedType propertyType = property.getType();
    if(isCollectionType(propertyType) && !isMap(propertyType)) {
      return Optional.fromNullable(collectionProperty(propertyType));
    }
    return Optional.absent();
  }

  private static Property collectionProperty(ResolvedType propertyType) {
    ResolvedType itemType = collectionElementType(propertyType);
    if (itemType != null) {
      if (Types.isBaseType(typeName(itemType))) {
        return new ArrayProperty(fromType(itemType));
      } else if (isCollectionType(itemType) && !isMap(propertyType)) {
        return new ArrayProperty(collectionProperty(itemType));
      } else {
        return new ArrayProperty(new RefProperty(typeName(itemType)));
      }
    }
    return null;
  }
}
