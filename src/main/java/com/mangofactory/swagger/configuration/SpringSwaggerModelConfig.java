package com.mangofactory.swagger.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

@Configuration
public class SpringSwaggerModelConfig {

   @Bean
   public Map<Class, String> defaultParameterDataTypes() {
      Map<Class, String> dataTypeMappings = newHashMap();
      dataTypeMappings.put(char.class, "string");
      dataTypeMappings.put(String.class, "string");
      dataTypeMappings.put(Integer.class, "int32");
      dataTypeMappings.put(int.class, "int32");
      dataTypeMappings.put(Long.class, "int64");
      dataTypeMappings.put(BigInteger.class, "int64");
      dataTypeMappings.put(long.class, "int64");
      dataTypeMappings.put(Float.class, "float");
      dataTypeMappings.put(float.class, "float");
      dataTypeMappings.put(Double.class, "double");
      dataTypeMappings.put(double.class, "double");
      dataTypeMappings.put(BigDecimal.class, "double");
      dataTypeMappings.put(Byte.class, "byte");
      dataTypeMappings.put(byte.class, "byte");
      dataTypeMappings.put(Boolean.class, "boolean");
      dataTypeMappings.put(boolean.class, "boolean");
      dataTypeMappings.put(Date.class, "date-time");
      return dataTypeMappings;
   }
}
