package com.mangofactory.documentation.swagger.mappers;

import com.mangofactory.documentation.schema.ModelProperty;
import com.wordnik.swagger.models.Model;
import com.wordnik.swagger.models.parameters.Parameter;
import com.wordnik.swagger.models.properties.BooleanProperty;
import com.wordnik.swagger.models.properties.DateProperty;
import com.wordnik.swagger.models.properties.DateTimeProperty;
import com.wordnik.swagger.models.properties.DecimalProperty;
import com.wordnik.swagger.models.properties.DoubleProperty;
import com.wordnik.swagger.models.properties.FloatProperty;
import com.wordnik.swagger.models.properties.IntegerProperty;
import com.wordnik.swagger.models.properties.LongProperty;
import com.wordnik.swagger.models.properties.ObjectProperty;
import com.wordnik.swagger.models.properties.Property;
import com.wordnik.swagger.models.properties.RefProperty;
import com.wordnik.swagger.models.properties.StringProperty;
import com.wordnik.swagger.models.properties.UUIDProperty;
import org.mapstruct.Mapper;
import org.mapstruct.TargetType;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Mapper
public abstract class ModelMapper {
  public Model resolve(com.mangofactory.documentation.schema.Model source,
                       @TargetType Class<? extends Model> entityClass) {
    throw new NotImplementedException();
  }

  public Parameter resolve(com.mangofactory.documentation.service.Parameter source,
                       @TargetType Class<? extends Parameter> entityClass) {
    throw new NotImplementedException();
  }

  public Property resolve(ModelProperty source, @TargetType Class<? extends Property> entityClass) {
    String typeName = source.getTypeName();
    if (isOfType(typeName, "void")) {
      return new ObjectProperty().title(source.getName());
    }
    if (isOfType(typeName, "int")) {
      return new IntegerProperty().title(source.getName());
    }
    if (isOfType(typeName, "long")) {
      return new LongProperty().title(source.getName());
    }
    if (isOfType(typeName, "float")) {
      return new FloatProperty().title(source.getName());
    }
    if (isOfType(typeName, "double")) {
      return new DoubleProperty().title(source.getName());
    }
    if (isOfType(typeName, "string")) {
      return new StringProperty();
    }
    if (isOfType(typeName, "byte")) {
      StringProperty byteArray = new StringProperty();
      byteArray.setFormat("byte");
      return byteArray.title(source.getName());
    }
    if (isOfType(typeName, "boolean")) {
      return new BooleanProperty().title(source.getName());
    }
    if (isOfType(typeName, "Date")) {
      return new DateProperty().title(source.getName());
    }
    if (isOfType(typeName, "DateTime") || isOfType(typeName, "date-time")) {
      return new DateTimeProperty().title(source.getName());
    }
    if (isOfType(typeName, "BigDecimal") || isOfType(typeName, "BigInteger")) {
      return new DecimalProperty().title(source.getName());
    }
    if (isOfType(typeName, "UUID")) {
      return new UUIDProperty().title(source.getName());
    }
    return new RefProperty(source.getTypeName()).title(source.getName());
  }

  private boolean isOfType(String initialType, String ofType) {
    return initialType.equalsIgnoreCase(ofType);
  }
}
