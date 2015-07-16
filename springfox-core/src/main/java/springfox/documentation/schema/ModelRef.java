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

package springfox.documentation.schema;

import com.google.common.base.Optional;
import springfox.documentation.service.AllowableValues;

public class ModelRef {
  private final String type;
  private final boolean isMap;
  private final Optional<String> itemType;
  private final Optional<AllowableValues> allowableValues;

  public ModelRef(String type, String itemType) {
    this(type, itemType, false);
  }

  public ModelRef(String type, String itemType, AllowableValues allowableValues) {
    this(type, itemType, allowableValues, false);
  }

  public ModelRef(String type, AllowableValues allowableValues) {
    this(type, null, allowableValues);
  }

  public ModelRef(String type, String itemType, boolean isMap) {
    this(type, itemType, null, isMap);
  }

  public ModelRef(String type, String itemType, AllowableValues allowableValues, boolean isMap) {
    this.type = type;
    this.isMap = isMap;
    this.allowableValues = Optional.fromNullable(allowableValues);
    this.itemType = Optional.fromNullable(itemType);
  }

  public ModelRef(String type) {
    this(type, null, null);
  }

  public String getType() {
    return type;
  }
  
  public boolean isCollection() {
    return itemType.isPresent() && !isMap;
  }

  public boolean isMap() {
    return itemType.isPresent() && isMap;
  }

  public String getItemType() {
    return itemType.orNull();
  }

  public AllowableValues getAllowableValues() {
    return allowableValues.orNull();
  }
}
