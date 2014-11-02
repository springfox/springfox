package com.mangofactory.swagger.mixins

import com.mangofactory.swagger.address.AbsoluteSwaggerAddressProvider
import com.mangofactory.swagger.address.RelativeSwaggerAddressProvider
import com.mangofactory.swagger.address.SwaggerAddressProvider

import javax.servlet.ServletContext

class SwaggerAddressProviderSupport {
  AbsoluteSwaggerAddressProvider absoluteSwaggerAddressProvider(){
      SwaggerAddressProvider swaggerPathProvider = new AbsoluteSwaggerAddressProvider();
      swaggerPathProvider.setApiResourcePrefix("api/v1");
      swaggerPathProvider.servletContext = [getContextPath: {return "/context-path"}] as ServletContext
      return swaggerPathProvider
   }

  RelativeSwaggerAddressProvider relativeSwaggerAddressProvider(){
      new RelativeSwaggerAddressProvider()
   }
}
