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

package springfox.documentation.spring.web.dummy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class DummyModels {

  public class BusinessModel {
    private String name;
    private String numEmployees;

    public BusinessModel() {
    }

    public String getNumEmployees() {
      return numEmployees;
    }

    public void setNumEmployees(String numEmployees) {
      this.numEmployees = numEmployees;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  public class AnnotatedBusinessModel {
//    @ApiModelProperty(value = "The name of this business", required = true)
    private String name;
    //        @ApiModelProperty(value = "Total number of current employees")
    private String numEmployees;

    @ApiModelProperty(value = "The name of this business", required = true)
    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    @ApiModelProperty(value = "Total number of current employees")
    public String getNumEmployees() {
      return numEmployees;
    }

    public void setNumEmployees(String numEmployees) {
      this.numEmployees = numEmployees;
    }
  }

  @ApiModel(value = "AlternateBusinessModelName", description = "Swagger annotated model")
  public class NamedBusinessModel extends BusinessModel {
  }

  public class CorporationModel extends BusinessModel {
  }

  public class Paginated<T> {

  }

  public class FunkyBusiness {
    private String id;
    private String name;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  public class ModelWithSerializeOnlyProperty {
    private String alwaysVisible;
    @JsonIgnore
    private Long visibleForSerialize;

    public String getAlwaysVisible() {
      return alwaysVisible;
    }


    public void setAlwaysVisible(String alwaysVisible) {
      this.alwaysVisible = alwaysVisible;
    }

    @JsonProperty
    @JsonInclude
    public Long getVisibleForSerialize() {
      return visibleForSerialize;
    }

    @JsonIgnore
    public void setVisibleForSerialize(Long visibleForSerialize) {
      this.visibleForSerialize = visibleForSerialize;
    }
  }

  @Target(ElementType.PARAMETER)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Ignorable {
  }

}
