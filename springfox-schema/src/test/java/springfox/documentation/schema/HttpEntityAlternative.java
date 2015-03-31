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

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public class HttpEntityAlternative<T> {
  public static final HttpEntity EMPTY = null;
  private final HttpHeaders headers = null;
  private final T body = null;

  public static HttpEntity getEmpty() {
    return EMPTY;
  }

  public HttpHeaders getHeaders() {
    return headers;
  }

  public T getBody() {
    return body;
  }
}
