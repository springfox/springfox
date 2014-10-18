package com.mangofactory.swagger.models.property;

import com.google.common.base.Optional;
import com.wordnik.swagger.models.properties.Property;
import com.wordnik.swagger.models.properties.StringProperty;

import java.util.List;

import static com.mangofactory.swagger.models.ResolvedTypes.*;

public class EnumProperties {
  public static Optional<? extends Property> from(ModelProperty property) {
    Optional<List<String>> enumValues = allowableValues(property.getType());
    if (enumValues.isPresent()) {
      return Optional.of(new StringProperty()._enum(enumValues.get()));
    }
    return Optional.absent();
  }
}
