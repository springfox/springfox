/*
 *
 *  * Copyright 2019-2020 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package springfox.test.contract.oas.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ApiUtil {
  private ApiUtil() {
  }

  public static void setExampleResponse(
      NativeWebRequest req,
      String contentType,
      String example) {
    try {
      req.getNativeResponse(HttpServletResponse.class).addHeader("Content-Type", contentType);
      req.getNativeResponse(HttpServletResponse.class).getOutputStream().print(example);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void checkApiKey(NativeWebRequest req) {
    if (!"1".equals(System.getenv("DISABLE_API_KEY"))
        && !"special-key".equals(req.getHeader("api_key"))) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing API key!");
    }
  }
}