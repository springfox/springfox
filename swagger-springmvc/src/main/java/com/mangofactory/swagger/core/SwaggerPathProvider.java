package com.mangofactory.swagger.core;

public interface SwaggerPathProvider {

   /**
    * e.g the api endpoint resides at  http://myapp.com:8080/<contextPath>/api/v1/businesses
    * Should return /api/v1 - note the leading and non trailing forward slashes
    *
    * @return the resource prefix of the api endpoint
    */
   public String getApiResourcePrefix();

   /**
    * return the absolute path to the app/web server hosting  the swagger documentation
    * swagger ui doesn't handle relative uri's correctly plus swagger code gen needs absolute uris
    * e.g. http://127.0.0.1:8080
    * https://mywebserver.com
    * @return the applications base uri
    */
   String getAppBasePath();

   String getSwaggerDocumentationBasePath();
}
