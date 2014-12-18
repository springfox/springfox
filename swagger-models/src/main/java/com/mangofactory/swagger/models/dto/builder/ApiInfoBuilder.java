package com.mangofactory.swagger.models.dto.builder;

import com.mangofactory.swagger.models.dto.ApiInfo;

public class ApiInfoBuilder {
  private String title;
  private String description;
  private String termsOfServiceUrl;
  private String contact;
  private String license;
  private String licenseUrl;

  public ApiInfoBuilder title(String title) {
    this.title = title;
    return this;
  }

  public ApiInfoBuilder description(String description) {
    this.description = description;
    return this;
  }

  public ApiInfoBuilder termsOfServiceUrl(String termsOfServiceUrl) {
    this.termsOfServiceUrl = termsOfServiceUrl;
    return this;
  }

  public ApiInfoBuilder contact(String contact) {
    this.contact = contact;
    return this;
  }

  public ApiInfoBuilder license(String license) {
    this.license = license;
    return this;
  }

  public ApiInfoBuilder licenseUrl(String licenseUrl) {
    this.licenseUrl = licenseUrl;
    return this;
  }

  public ApiInfo build() {
    return new ApiInfo(title, description, termsOfServiceUrl, contact, license, licenseUrl);
  }
}