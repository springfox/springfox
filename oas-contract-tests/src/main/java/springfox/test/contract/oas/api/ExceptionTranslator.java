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

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class ExceptionTranslator {

  private final org.springframework.boot.web.servlet.error.ErrorAttributes errorAttributes;

  public ExceptionTranslator(org.springframework.boot.web.servlet.error.ErrorAttributes errorAttributes) {
    this.errorAttributes = errorAttributes;
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public java.util.Map<String, Object> processConstraintViolationException(WebRequest request) {
    request.setAttribute(
        "javax.servlet.error.status_code",
        HttpStatus.BAD_REQUEST.value(),
        org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);
    return errorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());
  }
}
