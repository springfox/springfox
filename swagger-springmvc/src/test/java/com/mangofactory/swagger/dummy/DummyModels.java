package com.mangofactory.swagger.dummy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

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

  @ApiModel(value = "Swagger annotated model", description = "More descriptive model text")
  public class AnnotatedBusinessModel {
    //TODO - @ApiModelProperty has no effect on members - only setters
//        @ApiModelProperty(value = "The name of this business", required = true)
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
