package springfox.test.contract.oas.bugs;

import java.time.LocalDateTime;

public class Bug1965 {
  private LocalDateTime value;

  public LocalDateTime getValue() {
    return value;
  }

  public void setValue(LocalDateTime value) {
    this.value = value;
  }
}
