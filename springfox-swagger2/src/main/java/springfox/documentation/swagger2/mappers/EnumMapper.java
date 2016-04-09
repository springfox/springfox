/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package springfox.documentation.swagger2.mappers;

import com.google.common.base.Function;
import com.google.common.base.Optional;

import io.swagger.models.ModelImpl;
import io.swagger.models.parameters.SerializableParameter;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.LongProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableRangeValues;
import springfox.documentation.service.AllowableValues;

import java.util.List;

import static com.google.common.base.Optional.*;
import static com.google.common.collect.FluentIterable.*;
import static com.google.common.collect.Lists.*;

public class EnumMapper {
  static ModelImpl maybeAddEnumValues(ModelImpl toReturn, AllowableValues allowableValues) {
	  
    if (allowableValues instanceof AllowableListValues) {
      toReturn.setEnum(((AllowableListValues) allowableValues).getValues());
    }
    return toReturn;
  }
  static SerializableParameter maybeAddEnumValues(SerializableParameter toReturn, AllowableValues allowableValues) {
	  
    if (allowableValues instanceof AllowableListValues) {
      toReturn.setEnum(((AllowableListValues) allowableValues).getValues());
    }
    
    // fix  @ApiParam - Allowable values not displayed in Swagger API docs #1244 
    if (allowableValues instanceof AllowableRangeValues) {
    	AllowableRangeValues allowableRangeValues = (AllowableRangeValues)allowableValues;
    	if (allowableRangeValues.getMin()!=null) {
    		toReturn.setMinimum(Double.valueOf(allowableRangeValues.getMin()));	
    	}
        
        if (allowableRangeValues.getMax()!=null) {
        	toReturn.setMaximum(Double.valueOf(((AllowableRangeValues) allowableValues).getMax()));	
        }
        
        
      }
    
    return toReturn;
  }

  static Property maybeAddEnumValues(Property property, AllowableValues allowableValues) {
    if (allowableValues instanceof AllowableListValues) {
      if (property instanceof StringProperty) {
        StringProperty stringProperty = (StringProperty) property;
        AllowableListValues listValues = (AllowableListValues) allowableValues;
        stringProperty.setEnum(listValues.getValues());
      } else if (property instanceof IntegerProperty) {
        IntegerProperty integerProperty = (IntegerProperty) property;
        AllowableListValues listValues = (AllowableListValues) allowableValues;
        integerProperty.setEnum(convert(listValues.getValues(), Integer.class));
      } else if (property instanceof LongProperty) {
        LongProperty longProperty = (LongProperty) property;
        AllowableListValues listValues = (AllowableListValues) allowableValues;
        longProperty.setEnum(convert(listValues.getValues(), Long.class));
      }
    }
    return property;
  }

  private static <T extends Number> List<T> convert(List<String> values, Class<T> toType) {
    return newArrayList(presentInstances(from(values).transform(converterOfType(toType))));
  }

  private static <T extends Number> Function<? super String, Optional<T>> converterOfType(final Class<T> toType) {
    return new Function<String, Optional<T>>() {
      @SuppressWarnings("unchecked")
      @Override
      public Optional<T> apply(String input) {
        try {
          if (Integer.class.equals(toType)) {
            return (Optional<T>) Optional.of(new Integer(input));
          } else if (Long.class.equals(toType)) {
            return (Optional<T>) Optional.of(new Long(input));
          }
        } catch (NumberFormatException ignored) {
        }
        return Optional.absent();
      }
    };
  }
}
