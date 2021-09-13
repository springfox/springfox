package springfox.test.contract.oas.linkdedmodels.model;

import java.util.Objects;

public class Model01 extends Model  {

  private Model02 next;

  public Model02 getNext() {
    return next;
  }

  public void setNext(Model02 next) {
    this.next = next;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Model01 model01 = (Model01) o;
    return Objects.equals(next, model01.next) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(next, super.hashCode());
  }

  @Override
  public String toString() {
    return "Model01{" +
            "next=" + next +
            '}';
  }
}

