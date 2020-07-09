package springfox.test.contract.oas.features;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties("hidden")
public class Feature2613 {
  private String prop1;
  private int prop2;
  private BigDecimal hidden;

  public String getProp1() {
    return prop1;
  }

  public void setProp1(String prop1) {
    this.prop1 = prop1;
  }

  public int getProp2() {
    return prop2;
  }

  public void setProp2(int prop2) {
    this.prop2 = prop2;
  }

  public BigDecimal getHidden() {
    return hidden;
  }

  public void setHidden(BigDecimal hidden) {
    this.hidden = hidden;
  }
}
