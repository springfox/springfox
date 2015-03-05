package com.mangofactory.documentation.swagger2.mappers;

import com.mangofactory.documentation.service.ApiInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Mapper
public abstract class LicenseMapper {
  
  @License
  public com.wordnik.swagger.models.License fromLicenseUrl(ApiInfo from) {
    return new com.wordnik.swagger.models.License().name(from.getLicense()).url(from.getLicenseUrl());
  }


  @Qualifier
  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.SOURCE)
  public @interface LicenseTranslator {
  }

  @Qualifier
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.SOURCE)
  public @interface License {
  }
}
