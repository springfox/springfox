/*
 *
 *  Copyright 2015-2018 the original author or authors.
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

import org.springframework.core.SpringVersion;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.util.StringUtils.hasText;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromContextPath;

public class HostNameProvider {

    static final String X_FORWARDED_PREFIX = "X-Forwarded-Prefix";

    public HostNameProvider() {
        throw new UnsupportedOperationException();
    }

    static UriComponents componentsFrom(
            HttpServletRequest request,
            String basePath) {

        ServletUriComponentsBuilder builder = fromServletMapping(request, basePath);

        UriComponents components = UriComponentsBuilder.fromHttpRequest(
                new ServletServerHttpRequest(request))
                .build();

        String host = components.getHost();
        if (!hasText(host)) {
            return builder.build();
        }

        builder.host(host);
        builder.port(components.getPort());

        return builder.build();
    }

    private static ServletUriComponentsBuilder fromServletMapping(
            HttpServletRequest request,
            String basePath) {

        ServletUriComponentsBuilder builder = fromContextPath(request);

        builder.replacePath(prependForwardedPrefix(request, basePath));
        if (hasText(new UrlPathHelper().getPathWithinServletMapping(request))) {
            builder.path(request.getServletPath());
        }

        return builder;
    }

    private static String prependForwardedPrefix(
            HttpServletRequest request,
            String path) {

        String prefix = request.getHeader(X_FORWARDED_PREFIX);
        if (prefix != null) {
            if (isPreservePath(SpringVersion.getVersion())) {
                return prefix + path;
            } else {
                return prefix;
            }
        } else {
            return path;
        }
    }

    private static boolean isPreservePath(String version) {
        String[] versionComponents = version.split("\\.");
        int major = Integer.parseInt(versionComponents[0]);
        int minor = Integer.parseInt(versionComponents[1]);
        int micro = Integer.parseInt(versionComponents[2]);
        return major < 4 || (major == 4 && (minor < 3 || minor == 3 && micro < 15))
                || (major == 5 && minor == 0 && micro < 5);
    }
}
