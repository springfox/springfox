package com.mangofactory.swagger.core;

public interface SwaggerPathProvider {

   /**
    * e.g the api endpoint resides at  http://myapp.com:8080/<contextPath>/api/v1/businesses
    * Should return /api/v1/ - note the leading and trailing forward slashes
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
   public String getAppBasePath();

   /**
    *  The uri to the swagger documentation - typically the applications base path joined with the swagger path
    *  e.g. http://www.myserver.com/<contextPath>/api-docs
    * @return
    */
   public String getSwaggerDocumentationBasePath();
}
