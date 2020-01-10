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

package springfox.test.contract.swagger;

import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import springfox.test.contract.swagger.models.Pet;


@Component
public class InheritedServiceImpl implements InheritedService {

  @Override
  public String getSomething(String parameter) {
    return parameter;
  }

  @Override
  public Pet demonstrateInheritanceWithAnnotations(
      @ApiParam(value = "", required = true)
      @PathVariable("param1") String param1) {
    return null;
  }
}