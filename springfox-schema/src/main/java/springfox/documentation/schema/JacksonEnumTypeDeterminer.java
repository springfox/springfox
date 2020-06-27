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

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.schema.EnumTypeDeterminer;

@Component
public class JacksonEnumTypeDeterminer implements EnumTypeDeterminer {
  public boolean isEnum(Class<?> type) {
    if (type == null) {
      return false;
    }
    if (type.isEnum()) {
      JsonFormat annotation = type.getAnnotation(JsonFormat.class);
      if (annotation != null) {
        return !annotation.shape().equals(JsonFormat.Shape.OBJECT);
      } else {
        return true;
      }
    } else {
      return false;
    }
  }
}
