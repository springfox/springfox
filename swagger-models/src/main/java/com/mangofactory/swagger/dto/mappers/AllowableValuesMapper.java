package com.mangofactory.swagger.dto.mappers;


import com.mangofactory.swagger.dto.AllowableListValues;
import com.mangofactory.swagger.dto.AllowableRangeValues;
import com.mangofactory.swagger.dto.AllowableValues;
import org.mapstruct.Mapper;

import java.util.List;

import static com.google.common.collect.Lists.*;

@Mapper
public abstract class AllowableValuesMapper {

  //Allowable values related
  public abstract AllowableListValues toSwagger(com.mangofactory.service.model.AllowableListValues from);
  public abstract AllowableRangeValues toSwagger(com.mangofactory.service.model.AllowableRangeValues from);

  public List<com.mangofactory.swagger.dto.AllowableValues> ToSwagger(
          List<com.mangofactory.service.model.AllowableValues> list) {
    if (list == null) {
      return null;
    }

    List<AllowableValues> mapped = newArrayList();
    for (com.mangofactory.service.model.AllowableValues original : list) {
      mapped.add(concreteToSwagger(original));
    }

    return mapped;
  }

  public com.mangofactory.swagger.dto.AllowableValues concreteToSwagger(
          com.mangofactory.service.model.AllowableValues original) {

    if (original instanceof com.mangofactory.service.model.AllowableListValues) {
      return toSwagger((com.mangofactory.service.model.AllowableListValues) original);
    } else {
      return toSwagger((com.mangofactory.service.model.AllowableRangeValues) original);
    }
  }
}
