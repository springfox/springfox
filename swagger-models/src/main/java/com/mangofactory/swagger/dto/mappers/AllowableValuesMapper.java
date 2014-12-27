package com.mangofactory.swagger.dto.mappers;


import com.mangofactory.swagger.dto.AllowableListValues;
import com.mangofactory.swagger.dto.AllowableRangeValues;
import org.mapstruct.Mapper;

@Mapper
public abstract class AllowableValuesMapper {

  //Allowable values related
  public abstract AllowableListValues toSwaggerAllowableListValues(com.mangofactory.service.model.AllowableListValues from);
  public abstract AllowableRangeValues toSwaggerAllowableRangeValues(com.mangofactory.service.model.AllowableRangeValues from);

  public com.mangofactory.swagger.dto.AllowableValues toSwaggerAllowableValues(
          com.mangofactory.service.model.AllowableValues original) {
    if (original == null) {
      return null;
    }

    if (original instanceof com.mangofactory.service.model.AllowableListValues) {
      return toSwaggerAllowableListValues((com.mangofactory.service.model.AllowableListValues) original);
    } else if (original instanceof com.mangofactory.service.model.AllowableRangeValues) {
      return toSwaggerAllowableRangeValues((com.mangofactory.service.model.AllowableRangeValues) original);
    }
    throw new UnsupportedOperationException();
  }
}
