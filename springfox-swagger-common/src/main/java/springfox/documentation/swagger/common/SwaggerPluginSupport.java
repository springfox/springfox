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

package springfox.documentation.swagger.common;

import springfox.documentation.spi.DocumentationType;

public class SwaggerPluginSupport {
  private SwaggerPluginSupport() {
    throw new UnsupportedOperationException();
  }

  public static final String DOCUMENTATION_BASE_PATH = "/v1/api-docs";

  public static boolean pluginDoesApply(DocumentationType documentationType) {
    return DocumentationType.SWAGGER_12.equals(documentationType) || DocumentationType.SWAGGER_2.equals(documentationType);
  }
}
