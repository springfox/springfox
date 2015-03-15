package com.mangofactory.documentation.schema.property.provider;

import com.fasterxml.classmate.ResolvedType;
import com.mangofactory.documentation.schema.ModelProperty;
import com.mangofactory.documentation.schema.configuration.ObjectMapperConfigured;
import com.mangofactory.documentation.spi.schema.contexts.ModelContext;
import org.springframework.context.ApplicationListener;

import java.util.List;

public interface ModelPropertiesProvider extends ApplicationListener<ObjectMapperConfigured> {
  List<ModelProperty> propertiesFor(ResolvedType type, ModelContext givenContext);
}
