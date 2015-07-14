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

package springfox.documentation.spring.web.paths;

import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.service.PathAdjuster;
import springfox.documentation.spi.service.contexts.DocumentationContext;

public class PathMappingAdjuster implements PathAdjuster {
  private final DocumentationContext context;

  public PathMappingAdjuster(DocumentationContext context) {
    this.context = context;
  }

  @Override
  public String adjustedPath(String path) {
    return UriComponentsBuilder
        .fromPath(this.context.getPathMapping().or("/"))
        .path(path)
        .build()
        .toUriString();
  }
}
