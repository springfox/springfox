package com.mangofactory.documentation.swagger.mappers;

import com.fasterxml.classmate.ResolvedType;
import com.mangofactory.documentation.schema.Types;
import com.wordnik.swagger.models.ArrayModel;
import com.wordnik.swagger.models.Model;
import com.wordnik.swagger.models.ModelImpl;
import com.wordnik.swagger.models.RefModel;
import com.wordnik.swagger.models.parameters.BodyParameter;
import com.wordnik.swagger.models.parameters.CookieParameter;
import com.wordnik.swagger.models.parameters.HeaderParameter;
import com.wordnik.swagger.models.parameters.Parameter;
import com.wordnik.swagger.models.parameters.PathParameter;
import com.wordnik.swagger.models.parameters.QueryParameter;
import com.wordnik.swagger.models.properties.StringProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.mangofactory.documentation.schema.Collections.*;


@Component
public class ParameterMapper {
  
  @Autowired
  protected ModelMapper modelMapper;
  
  public Parameter resolve(com.mangofactory.documentation.service.Parameter source) {
    Parameter toReturn = null;
    if ("header".equalsIgnoreCase(source.getParamType())) {
      HeaderParameter param = new HeaderParameter()
              .array(source.isAllowMultiple())
              .description(source.getDescription())
              .name(source.getName());
      if (source.getType().isPresent() && isContainerType(source.getType().get())) {
        param.setArray(true);
        param.collectionFormat("csv");
        param.items(new StringProperty().title(source.getName()));
      } else {
        param.property(new StringProperty().title(source.getName()));
      }
      toReturn = param;
    } else if ("body".equalsIgnoreCase(source.getParamType())) {
      BodyParameter param = new BodyParameter()
              .description(source.getDescription())
              .name(source.getName())
              .schema(toSchema(source.getName(), source.getType().get()));
      toReturn = param;
    } else if ("path".equalsIgnoreCase(source.getParamType())) {
      PathParameter param = new PathParameter()
              .description(source.getDescription())
              .name(source.getName());
      if (source.getType().isPresent() && isContainerType(source.getType().get())) {
        param.setArray(true);
        param.collectionFormat("csv");
        param.items(new StringProperty().title(source.getName()));
      } else {
        param.property(new StringProperty().title(source.getName()));
      }
      toReturn = param;
    } else if ("query".equalsIgnoreCase(source.getParamType())) {
      QueryParameter param = new QueryParameter()
              .description(source.getDescription())
              .name(source.getName());
      if (source.getType().isPresent() && isContainerType(source.getType().get())) {
        param.setArray(true);
        param.collectionFormat("csv");
        param.items(new StringProperty().title(source.getName()));
      } else {
        param.property(new StringProperty().title(source.getName()));
      }
      toReturn = param;
    } else if ("cookie".equalsIgnoreCase(source.getParamType())) {
      CookieParameter param = new CookieParameter()
              .description(source.getDescription())
              .name(source.getName());
      if (source.getType().isPresent() && isContainerType(source.getType().get())) {
        param.setArray(true);
        param.collectionFormat("csv");
        param.items(new StringProperty().title(source.getName()));
      } else {
        param.property(new StringProperty().title(source.getName()));
      }
      toReturn = param;
    }
    return toReturn;
  }

  private Model toSchema(String name, ResolvedType type) {
    if (isContainerType(type)) {
      ResolvedType elementType = collectionElementType(type);
      return new ArrayModel().items(modelMapper.property(name, elementType.getErasedType().getSimpleName()));
    } else if (Types.isBaseType(type.getErasedType().getSimpleName())) {
      return new ModelImpl().property(name, modelMapper.property(name, type.getErasedType().getSimpleName()));
    }
    return new RefModel(type.getErasedType().getSimpleName());
  }
}
