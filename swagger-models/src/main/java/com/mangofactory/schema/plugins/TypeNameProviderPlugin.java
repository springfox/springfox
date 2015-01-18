package com.mangofactory.schema.plugins;

import org.springframework.plugin.core.Plugin;

public interface TypeNameProviderPlugin extends Plugin<DocumentationType> {
  public String nameFor(Class<?> type);
}
