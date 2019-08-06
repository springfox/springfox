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

package springfox.documentation.spring.web.csrf;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A mirror class of spring-security's `CsrfToken / server CsrfToken`
 *
 * @author liuxy
 */
public class MirrorCsrfToken {

    public static final MirrorCsrfToken EMPTY = new MirrorCsrfToken("", "", "");

    private final String token;

    private final String parameterName;

    private final String headerName;

    MirrorCsrfToken(String headerName, String parameterName, String token) {
        this.headerName = headerName;
        this.parameterName = parameterName;
        this.token = token;
    }

    @JsonProperty("headerName")
    public String getHeaderName() {
        return this.headerName;
    }

    @JsonProperty("parameterName")
    public String getParameterName() {
        return this.parameterName;
    }

    @JsonProperty("token")
    public String getToken() {
        return this.token;
    }
}
