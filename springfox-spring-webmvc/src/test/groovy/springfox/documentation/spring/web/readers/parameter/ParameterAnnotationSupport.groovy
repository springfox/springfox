package springfox.documentation.spring.web.readers.parameter

import io.swagger.annotations.ApiParam
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart

trait ParameterAnnotationSupport {
  RequestParam requestParam(required, value, defaultValue) {
    [
        value       : { -> value },
        defaultValue: { -> defaultValue },
        required    : { -> required }
    ] as RequestParam
  }

  PathVariable pathVariable(value, required = false) {
    [
        value   : { -> value },
        name    : { -> value },
        required: { -> required },
        annotationType: { PathVariable.class }
    ] as PathVariable
  }

  RequestHeader requestHeader(required, value, defaultValue) {
    [
        value       : { -> value },
        defaultValue: { -> defaultValue },
        required    : { -> required }
    ] as RequestHeader
  }

  RequestBody requestBody(required) {
    [
        required: { -> required }
    ] as RequestBody
  }

  RequestPart requestPart(required, value) {
    [
        value   : { -> value },
        required: { -> required }
    ] as RequestPart
  }

  ApiParam apiParam(required) {
    [
        required: { -> required }
    ] as ApiParam
  }
}
