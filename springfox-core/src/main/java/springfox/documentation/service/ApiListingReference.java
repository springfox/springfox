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

public class ApiListingReference {
  private final String path;
  private final String description;
  private final int position;

  public ApiListingReference(String path, String description, int position) {
    this.path = path;
    this.description = description;
    this.position = position;
  }

  public String getPath() {
    return path;
  }

  public String getDescription() {
    return description;
  }

  public int getPosition() {
    return position;
  }
}
