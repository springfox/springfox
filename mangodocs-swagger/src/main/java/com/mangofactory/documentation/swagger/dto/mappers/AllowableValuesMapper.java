package com.mangofactory.documentation.swagger.dto.mappers;


import com.mangofactory.documentation.service.model.AllowableValues;
import com.mangofactory.documentation.swagger.dto.AllowableListValues;
import com.mangofactory.documentation.swagger.dto.AllowableRangeValues;
import org.mapstruct.Mapper;

@Mapper
public abstract class AllowableValuesMapper {

  //Allowable values related
  public abstract AllowableListValues toSwaggerAllowableListValues(
          com.mangofactory.documentation.service.model.AllowableListValues from);

  public abstract AllowableRangeValues toSwaggerAllowableRangeValues(
          com.mangofactory.documentation.service.model.AllowableRangeValues from);

  public com.mangofactory.documentation.swagger.dto.AllowableValues toSwaggerAllowableValues(
          AllowableValues original) {
    if (original == null) {
      return null;
    }

    if (original instanceof com.mangofactory.documentation.service.model.AllowableListValues) {
      return toSwaggerAllowableListValues((com.mangofactory.documentation.service.model.AllowableListValues) original);
    } else if (original instanceof com.mangofactory.documentation.service.model.AllowableRangeValues) {
      return toSwaggerAllowableRangeValues((com.mangofactory.documentation.service.model.AllowableRangeValues)
              original);
    }
    throw new UnsupportedOperationException();
  }
}
