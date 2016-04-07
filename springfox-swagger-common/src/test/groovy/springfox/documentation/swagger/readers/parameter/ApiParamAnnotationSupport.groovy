package springfox.documentation.swagger.readers.parameter

import io.swagger.annotations.ApiParam

trait ApiParamAnnotationSupport {
  ApiParam apiParamWithAllowableValues(allowableValues) {
    [ allowableValues: { ->  allowableValues},
      name: { -> ""},
      value: { -> ""},
      access: { -> ""},
      defaultValue: { -> ""},
      allowMultiple: { -> true},
      required: { -> true}] as ApiParam
  }

  ApiParam apiParamWithAllowMultiple(allowableMultiple) {
    [ allowableValues: { ->  ""},
      name: { -> ""},
      value: { -> ""},
      access: { -> ""},
      defaultValue: { -> ""},
      allowMultiple: { -> allowableMultiple},
      required: { -> true}] as ApiParam
  }

  ApiParam apiParamWithRequired(required) {
    [ allowableValues: { ->  ""},
      name: { -> ""},
      value: { -> ""},
      access: { -> ""},
      defaultValue: { -> ""},
      allowMultiple: { -> false},
      required: { -> required}] as ApiParam
  }

  ApiParam apiParamWithNameAndValue(name, value) {
    [ allowableValues: { ->  ""},
      name: { -> name},
      value: { -> value},
      access: { -> ""},
      defaultValue: { -> ""},
      allowMultiple: { -> false},
      required: { -> false}] as ApiParam
  }

  ApiParam apiParamWithDefault(defaultValue) {
    [ allowableValues: { ->  ""},
      name: { -> ""},
      value: { -> ""},
      access: { -> ""},
      defaultValue: { -> defaultValue},
      allowMultiple: { -> false},
      required: { -> false}] as ApiParam
  }

  ApiParam apiParamWithAccess(access) {
    [ allowableValues: { ->  ""},
      name: { -> ""},
      value: { -> ""},
      access: { -> access},
      defaultValue: { -> ""},
      allowMultiple: { -> false},
      required: { -> false}] as ApiParam
  }

}
