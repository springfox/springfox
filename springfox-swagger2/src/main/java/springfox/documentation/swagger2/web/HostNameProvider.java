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
package springfox.documentation.swagger2.web;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.util.StringUtils.*;

public class HostNameProvider {

  public HostNameProvider() {
    throw new UnsupportedOperationException();
  }

  static UriComponents componentsFrom(final HttpServletRequest request, final String basePath) {
    ServletUriComponentsBuilder builder = fromServletMapping(request, basePath);

    ForwardedHeader forwarded = ForwardedHeader.of(request.getHeader(ForwardedHeader.NAME));
    String proto = hasText(forwarded.getProto()) ? forwarded.getProto() : request.getHeader("X-Forwarded-Proto");
    String forwardedSsl = request.getHeader("X-Forwarded-Ssl");

    if (hasText(proto)) {
      builder.scheme(proto);
    } else if (hasText(forwardedSsl) && forwardedSsl.equalsIgnoreCase("on")) {
      builder.scheme("https");
    }

    String host = forwarded.getHost();
    host = hasText(host) ? host : request.getHeader("X-Forwarded-Host");

    if (!hasText(host)) {
      return builder.build();
    }

    String[] hosts = commaDelimitedListToStringArray(host);
    String hostToUse = hosts[0];

    if (hostToUse.contains(":")) {

      String[] hostAndPort = split(hostToUse, ":");

      builder.host(hostAndPort[0]);
      builder.port(Integer.parseInt(hostAndPort[1]));

    } else {
      builder.host(hostToUse);
      builder.port(-1); // reset port if it was forwarded from default port
    }

    String port = request.getHeader("X-Forwarded-Port");

    if (hasText(port)) {
      builder.port(Integer.parseInt(port));
    }

    return builder.build();
  }

  private static ServletUriComponentsBuilder fromServletMapping(final HttpServletRequest request, final String basePath) {
    final ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromContextPath(request);

    builder.replacePath(prependForwardedPrefix(request, basePath));
    if (StringUtils.hasText(new UrlPathHelper().getPathWithinServletMapping(request))) {
      builder.path(request.getServletPath());
    }

    return builder;
  }

  private static String prependForwardedPrefix(final HttpServletRequest request, final String path) {
    final String prefix = request.getHeader("X-Forwarded-Prefix");
    if (prefix != null) {
      return prefix + path;
    } else {
      return path;
    }
  }
}
