package springfox.documentation.swagger.readers.parameter

import io.swagger.annotations.ApiParam
import io.swagger.annotations.Example
import io.swagger.annotations.ExampleProperty

trait ApiParamAnnotationSupport {
  ApiParam apiParamWithType(type, format) {
    [allowableValues : { -> "" },
     name            : { -> "" },
     value           : { -> "" },
     type            : { -> type },
     format          : { -> format },
     access          : { -> "" },
     defaultValue    : { -> "" },
     allowMultiple   : { -> true },
     allowEmptyValue : { -> false },
     required        : { -> true },
     collectionFormat: { -> "" },
     example         : { -> "" },
     examples        : { -> examples() },
     hidden          : { -> false }] as ApiParam
  }

  ApiParam apiParamWithAllowableValues(allowableValues) {
    [allowableValues : { -> allowableValues },
     name            : { -> "" },
     value           : { -> "" },
     type            : { -> "" },
     format          : { -> "" },
     access          : { -> "" },
     defaultValue    : { -> "" },
     allowMultiple   : { -> true },
     allowEmptyValue : { -> false },
     required        : { -> true },
     collectionFormat: { -> "" },
     example         : { -> "" },
     examples        : { -> examples() },
     hidden          : { -> false }] as ApiParam
  }

  ApiParam apiParamWithAllowMultiple(allowableMultiple) {
    [allowableValues : { -> "" },
     name            : { -> "" },
     value           : { -> "" },
     type            : { -> "" },
     format          : { -> "" },
     access          : { -> "" },
     defaultValue    : { -> "" },
     allowMultiple   : { -> allowableMultiple },
     allowEmptyValue : { -> false },
     required        : { -> true },
     collectionFormat: { -> "" },
     example         : { -> "" },
     examples        : { -> examples() },
     hidden          : { -> false }] as ApiParam
  }

  ApiParam apiParamWithRequired(required) {
    [allowableValues : { -> "" },
     name            : { -> "" },
     value           : { -> "" },
     type            : { -> "" },
     format          : { -> "" },
     access          : { -> "" },
     defaultValue    : { -> "" },
     allowMultiple   : { -> false },
     allowEmptyValue : { -> false },
     required        : { -> required },
     collectionFormat: { -> "" },
     example         : { -> "" },
     examples        : { -> examples() },
     hidden          : { -> false }] as ApiParam
  }

  ApiParam apiParamWithNameAndValue(name, value) {
    [allowableValues : { -> "" },
     name            : { -> name },
     value           : { -> value },
     type            : { -> "" },
     format          : { -> "" },
     access          : { -> "" },
     defaultValue    : { -> "" },
     allowMultiple   : { -> false },
     allowEmptyValue : { -> false },
     required        : { -> false },
     collectionFormat: { -> "" },
     example         : { -> "" },
     examples        : { -> examples() },
     hidden          : { -> false }] as ApiParam
  }

  ApiParam apiParamWithDefault(defaultValue) {
    [allowableValues : { -> "" },
     name            : { -> "" },
     value           : { -> "" },
     type            : { -> "" },
     format          : { -> "" },
     access          : { -> "" },
     defaultValue    : { -> defaultValue },
     allowMultiple   : { -> false },
     allowEmptyValue : { -> false },
     required        : { -> false },
     collectionFormat: { -> "" },
     example         : { -> "" },
     examples        : { -> examples() },
     hidden          : { -> false }] as ApiParam
  }

  ApiParam apiParamWithAccess(access) {
    [allowableValues : { -> "" },
     name            : { -> "" },
     value           : { -> "" },
     type            : { -> "" },
     format          : { -> "" },
     access          : { -> access },
     defaultValue    : { -> "" },
     allowMultiple   : { -> false },
     allowEmptyValue : { -> false },
     required        : { -> false },
     collectionFormat: { -> "" },
     example         : { -> "" },
     examples        : { -> examples() },
     hidden          : { -> false }] as ApiParam
  }

  ApiParam apiParamWithHidden(hidden) {
    [allowableValues : { -> "" },
     name            : { -> "" },
     value           : { -> "" },
     type            : { -> "" },
     format          : { -> "" },
     access          : { -> "" },
     defaultValue    : { -> "" },
     allowMultiple   : { -> false },
     allowEmptyValue : { -> false },
     required        : { -> false },
     collectionFormat: { -> "" },
     example         : { -> "" },
     examples        : { -> examples() },
     hidden          : { -> hidden }] as ApiParam
  }

  Example examples() {
    [
        value: { ->
          [
              [
                  mediaType: { -> "application/json" },
                  value    : { -> "{'hello': 'world'}" }
              ]
          ] as ExampleProperty[]
        }
    ] as Example
  }

}
