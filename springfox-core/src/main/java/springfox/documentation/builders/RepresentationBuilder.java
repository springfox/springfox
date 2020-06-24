package springfox.documentation.builders;

import springfox.documentation.service.Representation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RepresentationBuilder {
  private org.springframework.http.MediaType mediaType;
  private ModelSpecificationBuilder modelBuilder;
  private final Map<String, EncodingBuilder> encodings = new HashMap<>();

  private ModelSpecificationBuilder modelSpecificationBuilder() {
    if (modelBuilder == null) {
      this.modelBuilder = new ModelSpecificationBuilder();
    }
    return modelBuilder;
  }

  public RepresentationBuilder model(Consumer<ModelSpecificationBuilder> model) {
    model.accept(modelSpecificationBuilder());
    return this;
  }

  public RepresentationBuilder mediaType(String mediaType) {
    this.mediaType = org.springframework.http.MediaType.parseMediaType(mediaType);
    return this;
  }

  public RepresentationBuilder mediaType(org.springframework.http.MediaType mediaType) {
    this.mediaType = mediaType;
    return this;
  }

  public RepresentationBuilder clearEncodings() {
    this.encodings.clear();
    return this;
  }

  private EncodingBuilder encodingForProperty(String property) {
    return this.encodings.computeIfAbsent(
        property,
        p -> new EncodingBuilder()
            .propertyRef(p));
  }

  public Function<Consumer<EncodingBuilder>, RepresentationBuilder> encoding(String property) {
    return encoding -> {
      encoding.accept(encodingForProperty(property));
      return this;
    };
  }


  public Representation build() {
    return new Representation(
        mediaType,
        modelBuilder.build(),
        encodings.values()
            .stream()
            .map(EncodingBuilder::build)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet()));
  }

  public RepresentationBuilder copyOf(Representation other) {
    if (other != null) {
      other.getEncodings().forEach(e -> this.encoding(e.getPropertyRef())
          .apply(encoding -> encoding.copyOf(e)));
      this.mediaType(other.getMediaType())
          .model(m -> m.copyOf(other.getModel()));
    }
    return this;
  }
}