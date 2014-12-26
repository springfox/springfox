package com.mangofactory.swagger.dto.mappers;


import com.mangofactory.swagger.dto.AllowableListValues;
import com.mangofactory.swagger.dto.AllowableRangeValues;
import org.mapstruct.Mapper;

@Mapper
public abstract class AllowableValuesMapper {

  //Allowable values related
  public abstract AllowableListValues toSwagger(com.mangofactory.service.model.AllowableListValues from);
  public abstract AllowableRangeValues toSwagger(com.mangofactory.service.model.AllowableRangeValues from);

  public com.mangofactory.swagger.dto.AllowableValues concreteToSwagger(
          com.mangofactory.service.model.AllowableValues original) {

    if (original instanceof com.mangofactory.service.model.AllowableListValues) {
      return toSwagger((com.mangofactory.service.model.AllowableListValues) original);
    } else {
      return toSwagger((com.mangofactory.service.model.AllowableRangeValues) original);
    }
  }
}
