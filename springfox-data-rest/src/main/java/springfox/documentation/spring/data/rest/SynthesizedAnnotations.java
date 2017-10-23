/*
 *
 *  Copyright 2016 the original author or authors.
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
package springfox.documentation.spring.data.rest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.lang.annotation.Annotation;

class SynthesizedAnnotations {
  private SynthesizedAnnotations() {
    throw new UnsupportedOperationException();
  }

  static final RequestBody REQUEST_BODY_ANNOTATION = new RequestBody() {
    @Override
    public Class<? extends Annotation> annotationType() {
      return RequestBody.class;
    }

    @Override
    public boolean required() {
      return true;
    }
  };

  static final PathVariable PATH_VARIABLE_ANNOTATION = pathVariable("id");

  static final ApiIgnore API_IGNORE_ANNOTATION = new ApiIgnore() {
    @Override
    public String value() {
      return "Parameter is ignored";
    }

    @Override
    public Class<? extends Annotation> annotationType() {
      return ApiIgnore.class;
    }
  };

  static PathVariable pathVariable(final String parameterName) {
    return new PathVariable() {
      @Override
      public Class<? extends Annotation> annotationType() {
        return PathVariable.class;
      }

      @Override
      public String value() {
        return parameterName;
      }

      @Override
      public String name() {
        return parameterName;
      }

      @Override
      public boolean required() {
        return true;
      }
    };
  }

  static RequestParam requestParam(final String parameterName) {
    return new RequestParam() {
      @Override
      public Class<? extends Annotation> annotationType() {
        return RequestParam.class;
      }

      @Override
      public String value() {
        return parameterName;
      }

      @Override
      public String name() {
        return parameterName;
      }

      @Override
      public boolean required() {
        return false;
      }

      @Override
      public String defaultValue() {
        return null;
      }
    };
  }
}
