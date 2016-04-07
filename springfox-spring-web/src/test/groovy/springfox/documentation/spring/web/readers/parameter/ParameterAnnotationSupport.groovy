package springfox.documentation.spring.web.readers.parameter

import org.springframework.web.bind.annotation.*

trait ParameterAnnotationSupport {
  RequestParam requestParam(required, value, defaultValue) {
    [ value: { -> value},
      defaultValue: { -> defaultValue},
      required: { -> required}] as RequestParam
  }

  PathVariable pathVariable(value) {
    [value: { -> value}] as PathVariable
  }

  RequestHeader requestHeader(required, value, defaultValue) {
    [value: { -> value},
     defaultValue: { -> defaultValue},
     required: { -> required}] as RequestHeader
  }

  RequestBody requestBody(required) {
    [required: { -> required}] as RequestBody
  }

  RequestPart requestPart(required, value) {
    [value: { -> value},
     required: { -> required}] as RequestPart
  }

}
