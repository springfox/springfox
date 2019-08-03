package springfox.bean.validators.plugins

import javax.validation.constraints.*

trait AnnotationsSupport {
  Size size(min, max) {
    [ min: { -> min},
      max: { -> max}] as Size
  }
  NotNull notNull() {
    [] as NotNull
  }
  NotBlank notBlank() {
    [] as NotBlank
  }
  Min min(value) {
    [value: { -> value}] as Min
  }
  DecimalMin decimalMin(value) {
    [value: { -> value}] as DecimalMin
  }
  Max max(value) {
    [value: { -> value}] as Max
  }
  DecimalMax decimalMax(value) {
    [value: { -> value}] as DecimalMax
  }
  Pattern pattern(regexp) {
    [regexp: { -> regexp}] as Pattern
  }
}
