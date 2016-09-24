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

import com.google.common.base.Predicate;
import org.springframework.data.rest.webmvc.RepositoryController;
import org.springframework.data.rest.webmvc.alps.AlpsController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Map;

import static com.google.common.base.Predicates.or;

class EntityServices {
  private EntityServices() {
    throw new UnsupportedOperationException();
  }

  static Predicate<Map.Entry<RequestMappingInfo, HandlerMethod>> restDataServices() {
    return or(repositories(), entityServices(), entitySearchServices(), entityRelationshipsServices());
  }

  static Predicate<Map.Entry<RequestMappingInfo, HandlerMethod>> repositories() {
    return new Predicate<Map.Entry<RequestMappingInfo, HandlerMethod>>() {
      @Override
      public boolean apply(Map.Entry<RequestMappingInfo, HandlerMethod> input) {
        return RepositoryController.class.equals(input.getValue().getBeanType());
      }
    };
  }

  static Predicate<Map.Entry<RequestMappingInfo, HandlerMethod>> entityServices() {
    return new Predicate<Map.Entry<RequestMappingInfo, HandlerMethod>>() {
      @Override
      public boolean apply(Map.Entry<RequestMappingInfo, HandlerMethod> input) {
        return input.getValue().getBeanType().getSimpleName().equals("RepositoryEntityController");
      }
    };
  }

  static Predicate<Map.Entry<RequestMappingInfo, HandlerMethod>> entitySearchServices() {
    return new Predicate<Map.Entry<RequestMappingInfo, HandlerMethod>>() {
      @Override
      public boolean apply(Map.Entry<RequestMappingInfo, HandlerMethod> input) {
        return input.getValue().getBeanType().getSimpleName().equals("RepositorySearchController");
      }
    };
  }

  static Predicate<Map.Entry<RequestMappingInfo, HandlerMethod>> entityRelationshipsServices() {
    return new Predicate<Map.Entry<RequestMappingInfo, HandlerMethod>>() {
      @Override
      public boolean apply(Map.Entry<RequestMappingInfo, HandlerMethod> input) {
        return input.getValue().getBeanType().getSimpleName().equals("RepositoryPropertyReferenceController");
      }
    };
  }

  static Predicate<Map.Entry<RequestMappingInfo, HandlerMethod>> entitySchemaService() {
    return new Predicate<Map.Entry<RequestMappingInfo, HandlerMethod>>() {
      @Override
      public boolean apply(Map.Entry<RequestMappingInfo, HandlerMethod> input) {
        return input.getValue().getBeanType().getSimpleName().equals("RepositorySchemaController");
      }
    };
  }

  static Predicate<Map.Entry<RequestMappingInfo, HandlerMethod>> alpsProfileServices() {
    return new Predicate<Map.Entry<RequestMappingInfo, HandlerMethod>>() {
      @Override
      public boolean apply(Map.Entry<RequestMappingInfo, HandlerMethod> input) {
        return AlpsController.class.equals(input.getValue().getBeanType());
      }
    };
  }
}
