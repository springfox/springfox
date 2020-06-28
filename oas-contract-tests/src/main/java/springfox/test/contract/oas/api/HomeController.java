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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import static org.springframework.util.AntPathMatcher.*;
import static org.springframework.web.servlet.view.UrlBasedViewResolver.*;

/**
 * Home redirection to swagger api documentation
 */
@Controller
public class HomeController {

  @Value("${springfox.documentation.swagger-ui.base-url:}/swagger-ui/")
  private String swaggerUiPath;

  @GetMapping(DEFAULT_PATH_SEPARATOR)
  public String index() {
    return REDIRECT_URL_PREFIX + swaggerUiPath;
  }
}
