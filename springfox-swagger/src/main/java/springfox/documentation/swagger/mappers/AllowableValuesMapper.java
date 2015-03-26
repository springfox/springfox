package springfox.documentation.swagger.mappers;


import org.mapstruct.Mapper;
import springfox.documentation.swagger.dto.AllowableListValues;
import springfox.documentation.swagger.dto.AllowableRangeValues;

@Mapper
public abstract class AllowableValuesMapper {

  //Allowable values related
  public abstract AllowableListValues toSwaggerAllowableListValues(
          springfox.documentation.service.AllowableListValues from);

  public abstract AllowableRangeValues toSwaggerAllowableRangeValues(
          springfox.documentation.service.AllowableRangeValues from);

  public springfox.documentation.swagger.dto.AllowableValues toSwaggerAllowableValues(
          springfox.documentation.service.AllowableValues original) {
    if (original == null) {
      return null;
    }

    if (original instanceof springfox.documentation.service.AllowableListValues) {
      return toSwaggerAllowableListValues((springfox.documentation.service.AllowableListValues) original);
    } else if (original instanceof springfox.documentation.service.AllowableRangeValues) {
      return toSwaggerAllowableRangeValues((springfox.documentation.service.AllowableRangeValues)
              original);
    }
    throw new UnsupportedOperationException();
  }
}