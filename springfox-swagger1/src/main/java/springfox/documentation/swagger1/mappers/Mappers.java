/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

package springfox.documentation.swagger1.mappers;

import org.springframework.web.util.UriComponents;
import springfox.documentation.swagger1.dto.ApiListing;

import javax.servlet.http.HttpServletRequest;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.*;
import static org.springframework.util.StringUtils.*;
import static springfox.documentation.swagger.common.HostNameProvider.*;

public class Mappers {

  private Mappers() {
    throw new UnsupportedOperationException();
  }

  public static Function<Map.Entry<String, List<springfox.documentation.service.ApiListing>>, Map.Entry<String,
      List<ApiListing>>>
  toApiListingDto(
      final HttpServletRequest servletRequest,
      final String host,
      final ServiceModelToSwaggerMapper mapper) {

    return entry -> {
      List<ApiListing> newApiListings = entry.getValue().stream().map(value -> {
        ApiListing apiListing = mapper.toSwaggerApiListing(value);
        UriComponents uriComponents = componentsFrom(servletRequest, apiListing.getBasePath());
        apiListing.setBasePath(adjustedBasePath(uriComponents, host, apiListing.getBasePath()));
        return apiListing;
      }).collect(toList());
      return new AbstractMap.SimpleEntry<>(entry.getKey(), newApiListings);
    };
  }


  private static String adjustedBasePath(
      UriComponents uriComponents,
      String hostNameOverride,
      String basePath) {
    if (!isEmpty(hostNameOverride)) {
      int port = uriComponents.getPort();
      if (port > -1) {
        return String.format("%s://%s:%d%s", uriComponents.getScheme(), hostNameOverride, port, basePath);
      }
      return String.format("%s://%s%s", uriComponents.getScheme(), hostNameOverride, basePath);
    }
    return basePath;
  }
}
