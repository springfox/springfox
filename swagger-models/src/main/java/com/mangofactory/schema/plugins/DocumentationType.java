package com.mangofactory.schema.plugins;

import org.springframework.http.MediaType;
import org.springframework.plugin.metadata.SimplePluginMetadata;

public class DocumentationType extends SimplePluginMetadata {
  public static final DocumentationType SWAGGER_12 = new DocumentationType("swagger", "1.2");
  private final MediaType mediaType;

  /**
   * Creates a new instance of {@code SimplePluginMetadata}.
   *  @param name    must not be {@literal null}.
   * @param version must not be {@literal null}.
   * @param mediaType must not be {@literal null}
   */
  public DocumentationType(String name, String version, MediaType mediaType) {
    super(name, version);
    this.mediaType = mediaType;
  }

  public DocumentationType(String name, String version) {
    this(name, version, MediaType.APPLICATION_JSON);
  }
}
