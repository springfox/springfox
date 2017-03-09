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

package springfox.documentation.service;

import org.springframework.util.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class ApiKey extends SecurityScheme {
  private final String keyname;
  private final String passAs;
  private Map<String, Object> vendorExtensions = new LinkedHashMap<String, Object>();

  public ApiKey(String name, String keyname, String passAs) {
    super(name, "apiKey");
    this.keyname = keyname;
    this.passAs = passAs;
  }

  public ApiKey(String name, String keyname, String passAs, Map<String, Object> vendorExtensions) {
    this(name, keyname, passAs);
    if(!CollectionUtils.isEmpty(vendorExtensions)) {
      for(String key : vendorExtensions.keySet()) {
        if (key.startsWith("x-")) {
          this.vendorExtensions.put(key, vendorExtensions.get(key));
        }
      }
    }
  }

  public String getKeyname() {
    return keyname;
  }

  public String getPassAs() {
    return passAs;
  }

  public Map<String, Object> getVendorExtensions() {
    return vendorExtensions;
  }
}
