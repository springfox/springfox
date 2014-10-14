package com.mangofactory.swagger.models.property;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Optional;
import com.wordnik.swagger.models.properties.Property;

import static com.mangofactory.swagger.models.Collections.*;

public class MapProperties {
  public static Optional<? extends Property> from(ModelProperty property) {
    ResolvedType propertyType = property.getType();
    if(isCollectionType(propertyType) && isMap(propertyType)) {
      return Optional.fromNullable(collectionProperty(propertyType));
    }
    return Optional.absent();
  }

  private static Property collectionProperty(ResolvedType propertyType) {
//    ResolvedType itemType = collectionElementType(propertyType);
//    if (itemType != null) {
//      if (Types.isBaseType(typeName(itemType))) {
//        return new ModelImpl().additionalProperties(SimpleProperties.fromType(itemType));
//      } else if (isCollectionType(itemType) && isMap(propertyType)) {
//        return new ArrayProperty(collectionProperty(itemType));
//      } else {
//        return new RefProperty(typeName(itemType)));
//      }
//    }
    return null;
  }
}
