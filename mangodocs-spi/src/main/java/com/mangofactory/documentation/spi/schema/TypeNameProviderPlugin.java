package com.mangofactory.documentation.spi.schema;

import com.mangofactory.documentation.spi.DocumentationType;
import org.springframework.plugin.core.Plugin;

public interface TypeNameProviderPlugin extends Plugin<DocumentationType> {
  public String nameFor(Class<?> type);
}
