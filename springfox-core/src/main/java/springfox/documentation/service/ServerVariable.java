/*
 *
 *  Copyright 2017-2018 the original author or authors.
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
package springfox.documentation.service;

import java.util.ArrayList;
import java.util.List;

public class ServerVariable {
  private List<String> allowedValues = new ArrayList<String>();
  private String defaultValue = null;
  private String description = null;
  private List<VendorExtension> extensions = null;

  public ServerVariable(
      List<String> allowedValues,
      String defaultValue,
      String description,
      List<VendorExtension> extensions) {
    this.allowedValues = allowedValues;
    this.defaultValue = defaultValue;
    this.description = description;
    this.extensions = extensions;
  }

  public List<String> getAllowedValues() {
    return allowedValues;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public String getDescription() {
    return description;
  }

  public List<VendorExtension> getExtensions() {
    return extensions;
  }
}

