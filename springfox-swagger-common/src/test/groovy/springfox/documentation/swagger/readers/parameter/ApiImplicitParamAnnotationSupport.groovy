package springfox.documentation.swagger.readers.parameter

import io.swagger.annotations.ApiImplicitParam

trait ApiImplicitParamAnnotationSupport {
  ApiImplicitParam apiParamWithType(type, format) {
    apiImplicitParam(type, format)
  }

  ApiImplicitParam apiParamWithDataType(dataType) {
    apiImplicitParam("", "", dataType)
  }

  ApiImplicitParam apiParamWithDataTypeClass(dataTypeClass) {
    apiImplicitParam("", "", "", dataTypeClass)
  }

  ApiImplicitParam collectionApiImplicitParam(
      type = "",
      format = "",
      dataType = "",
      dataTypeClass = Void.class) {
    [name         : { -> "test" },
     type         : { -> type },
     format       : { -> format },
     dataType     : { -> dataType },
     dataTypeClass: { -> dataTypeClass },
     allowMultiple: { -> true }
    ] as ApiImplicitParam
  }

  ApiImplicitParam apiImplicitParam(
      type = "",
      format = "",
      dataType = "",
      dataTypeClass = Void.class) {
    [name         : { -> "test" },
     type         : { -> type },
     format       : { -> format },
     dataType     : { -> dataType },
     dataTypeClass: { -> dataTypeClass },
     allowMultiple: { -> false }
    ] as ApiImplicitParam
  }

}
