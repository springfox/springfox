package com.mangofactory.documentation.swagger.mappers;


import com.mangofactory.documentation.service.AllowableValues;
import com.mangofactory.documentation.swagger.dto.AllowableListValues;
import com.mangofactory.documentation.swagger.dto.AllowableRangeValues;
import org.mapstruct.Mapper;

@Mapper
public abstract class AllowableValuesMapper {

  //Allowable values related
  public abstract AllowableListValues toSwaggerAllowableListValues(
          com.mangofactory.documentation.service.AllowableListValues from);

  public abstract AllowableRangeValues toSwaggerAllowableRangeValues(
          com.mangofactory.documentation.service.AllowableRangeValues from);

  public com.mangofactory.documentation.swagger.dto.AllowableValues toSwaggerAllowableValues(
          AllowableValues original) {
    if (original == null) {
      return null;
    }

    if (original instanceof com.mangofactory.documentation.service.AllowableListValues) {
      return toSwaggerAllowableListValues((com.mangofactory.documentation.service.AllowableListValues) original);
    } else if (original instanceof com.mangofactory.documentation.service.AllowableRangeValues) {
      return toSwaggerAllowableRangeValues((com.mangofactory.documentation.service.AllowableRangeValues)
              original);
    }
    throw new UnsupportedOperationException();
  }
}
