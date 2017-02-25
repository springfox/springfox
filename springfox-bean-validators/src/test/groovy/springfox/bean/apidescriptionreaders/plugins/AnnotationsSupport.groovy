package springfox.bean.apidescriptionreaders.plugins

import io.swagger.annotations.ApiParam


trait AnnotationsSupport {
  ApiParam apiParam(value) {
    [allowableValues: { -> "" },
     name           : { -> "" },
     value          : { -> value },
     access         : { -> "" },
     defaultValue   : { -> "" },
     allowMultiple  : { -> true },
     required       : { -> true },
     hidden         : { -> false }] as ApiParam
  }
}