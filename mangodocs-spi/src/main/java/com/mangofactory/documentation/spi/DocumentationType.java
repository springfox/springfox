package com.mangofactory.documentation.spi;

import org.springframework.http.MediaType;
import org.springframework.plugin.metadata.SimplePluginMetadata;

public class DocumentationType extends SimplePluginMetadata {
  public static final DocumentationType SWAGGER_12 = new DocumentationType("swagger", "1.2");
  public static final DocumentationType SPRING_WEB = new DocumentationType("spring-web", "1.0");
  private final MediaType mediaType;

  /**
   * Creates a new instance of {@code SimplePluginMetadata}.
   *
   * @param name      must not be {@literal null}.
   * @param version   must not be {@literal null}.
   * @param mediaType must not be {@literal null}
   */
  public DocumentationType(String name, String version, MediaType mediaType) {
    super(name, version);
    this.mediaType = mediaType;
  }

  public DocumentationType(String name, String version) {
    this(name, version, MediaType.APPLICATION_JSON);
  }

  public MediaType getMediaType() {
    return mediaType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DocumentationType)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    DocumentationType that = (DocumentationType) o;

    return mediaType.equals(that.mediaType);

  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + mediaType.hashCode();
    return result;
  }
}
