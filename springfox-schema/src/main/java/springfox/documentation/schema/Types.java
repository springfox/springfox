/*
 *
 *  Copyright 2015-2016 the original author or authors.
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

package springfox.documentation.schema;

import com.fasterxml.classmate.ResolvedType;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public class Types {

  private static final Set<String> baseTypes = new HashSet<>(Arrays.asList(
      "int",
      "date",
      "string",
      "double",
      "float",
      "boolean",
      "byte",
      "object",
      "long",
      "date-time",
      "__file",
      "biginteger",
      "bigdecimal"));
  
  private static final Map<Type, String> typeNameLookup = new HashMap<Type,String>();
  static {
    typeNameLookup.put(Long.TYPE, "long");
    typeNameLookup.put(Short.TYPE, "int");
    typeNameLookup.put(Integer.TYPE, "int");
    typeNameLookup.put(Double.TYPE, "double");
    typeNameLookup.put(Float.TYPE, "float");
    typeNameLookup.put(Byte.TYPE, "byte");
    typeNameLookup.put(Boolean.TYPE, "boolean");
    typeNameLookup.put(Character.TYPE, "string");

    typeNameLookup.put(Date.class, "date-time");
    typeNameLookup.put(java.sql.Date.class, "date");
    typeNameLookup.put(String.class, "string");
    typeNameLookup.put(Object.class, "object");
    typeNameLookup.put(Long.class, "long");
    typeNameLookup.put(Integer.class, "int");
    typeNameLookup.put(Short.class, "int");
    typeNameLookup.put(Double.class, "double");
    typeNameLookup.put(Float.class, "float");
    typeNameLookup.put(Boolean.class, "boolean");
    typeNameLookup.put(Byte.class, "byte");
    typeNameLookup.put(BigDecimal.class, "bigdecimal");
    typeNameLookup.put(BigInteger.class, "biginteger");
    typeNameLookup.put(Currency.class, "string");
    typeNameLookup.put(UUID.class, "string");
    typeNameLookup.put(MultipartFile.class, "__file");
  };
  
  private Types() {
    throw new UnsupportedOperationException();
  }

  public static String typeNameFor(Type type) {
    return typeNameLookup.get(type);
  }

  public static boolean isBaseType(String typeName) {
    return baseTypes.contains(typeName);
  }

  public static boolean isBaseType(ResolvedType type) {
    return baseTypes.contains(typeNameFor(type.getErasedType()));
  }

  public static boolean isVoid(ResolvedType returnType) {
    return Void.class.equals(returnType.getErasedType()) || Void.TYPE.equals(returnType.getErasedType());
  }
}
