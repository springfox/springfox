package com.mangofactory.swagger.models.property;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Optional;
import com.mangofactory.swagger.models.Types;
import com.wordnik.swagger.models.properties.Property;
import com.wordnik.swagger.models.properties.RefProperty;

import static com.mangofactory.swagger.models.Collections.*;
import static com.mangofactory.swagger.models.ResolvedTypes.*;

public class RefProperties {
  public static Optional<? extends Property> from(ModelProperty property) {
    ResolvedType propertyType = property.getType();
    if (isCollectionType(propertyType) || Types.isBaseType(typeName(propertyType))) {
      return Optional.absent();
    }
    return Optional.of(new RefProperty(typeName(propertyType)));
  }
}
