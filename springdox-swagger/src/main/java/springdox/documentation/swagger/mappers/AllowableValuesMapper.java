package springdox.documentation.swagger.mappers;


import org.mapstruct.Mapper;
import springdox.documentation.service.AllowableValues;
import springdox.documentation.swagger.dto.AllowableListValues;
import springdox.documentation.swagger.dto.AllowableRangeValues;

@Mapper
public abstract class AllowableValuesMapper {

  //Allowable values related
  public abstract AllowableListValues toSwaggerAllowableListValues(
          springdox.documentation.service.AllowableListValues from);

  public abstract AllowableRangeValues toSwaggerAllowableRangeValues(
          springdox.documentation.service.AllowableRangeValues from);

  public springdox.documentation.swagger.dto.AllowableValues toSwaggerAllowableValues(
          AllowableValues original) {
    if (original == null) {
      return null;
    }

    if (original instanceof springdox.documentation.service.AllowableListValues) {
      return toSwaggerAllowableListValues((springdox.documentation.service.AllowableListValues) original);
    } else if (original instanceof springdox.documentation.service.AllowableRangeValues) {
      return toSwaggerAllowableRangeValues((springdox.documentation.service.AllowableRangeValues)
              original);
    }
    throw new UnsupportedOperationException();
  }
}