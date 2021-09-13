package springfox.test.contract.oas.linkdedmodels.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;


@JsonTypeInfo(use = NAME, include = EXISTING_PROPERTY, property = "name", visible = true)
@JsonSubTypes({
        @Type(value = Model01.class, name = "Model01"),
        @Type(value = Model02.class, name = "Model02"),
        @Type(value = Model03.class, name = "Model03"),
        @Type(value = Model04.class, name = "Model04"),
        @Type(value = Model05.class, name = "Model05"),
        @Type(value = Model06.class, name = "Model06"),
        @Type(value = Model07.class, name = "Model07"),
        @Type(value = Model08.class, name = "Model08"),
        @Type(value = Model09.class, name = "Model09"),
        @Type(value = Model10.class, name = "Model10"),
        @Type(value = Model11.class, name = "Model11"),
        @Type(value = Model12.class, name = "Model12"),
        @Type(value = Model13.class, name = "Model13"),
        @Type(value = Model14.class, name = "Model14"),
        @Type(value = Model15.class, name = "Model15"),
        @Type(value = Model16.class, name = "Model15"),
        @Type(value = Model17.class, name = "Model17"),
        @Type(value = Model18.class, name = "Model18"),
        @Type(value = Model19.class, name = "Model19"),
        @Type(value = Model20.class, name = "Model20")
})
public abstract class Model   {

  @ApiModelProperty(required = true)
  @NotNull
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Model model = (Model) o;
    return Objects.equals(name, model.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return "Model{" +
            "name='" + name + '\'' +
            '}';
  }

}

