package com.mangofactory.documentation.schema;


import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.schema.TypeNameProviderPlugin;

public class DefaultTypeNameProvider implements TypeNameProviderPlugin {

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  @Override
  public String nameFor(Class<?> type) {
    return type.getSimpleName();
  }
}
