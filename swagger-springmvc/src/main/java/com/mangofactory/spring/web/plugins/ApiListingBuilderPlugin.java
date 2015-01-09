package com.mangofactory.spring.web.plugins;

import com.mangofactory.schema.plugins.DocumentationType;
import org.springframework.plugin.core.Plugin;

public interface ApiListingBuilderPlugin extends Plugin<DocumentationType> {
  public void apply(ApiListingContext apiListingContext);
}
