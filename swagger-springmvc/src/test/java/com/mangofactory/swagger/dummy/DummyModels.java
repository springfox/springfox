package com.mangofactory.swagger.dummy;

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
        @ApiModelProperty(value = "The name of this business", required = true)
        private String name;
        @ApiModelProperty(value = "Total number of current employees")
        private String numEmployees;

        public String getName() {
            return name;
        }

        @ApiModelProperty(value = "The name of this business", required = true)
        public void setName(String name) {
            this.name = name;
        }

        public String getNumEmployees() {
            return numEmployees;
        }

        @ApiModelProperty(value = "Total number of current employees")
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

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Ignorable {
    }

}
