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
   public String getAppBasePath();

    /**
    * Gets a uri friendly path from a request mapping pattern.
    * Typically involves removing any regex patterns or || conditions from a spring request mapping
    * This method will be called to resolve every request mapping endpoint.
    * A good extension point if you need to alter endpoints by adding or removing path segments.
    * Note: this should not be an absolute  uri
    *
    * @see com.mangofactory.swagger.readers.ApiDescriptionReader
    * @param requestMappingPattern
    * @return the request mapping endpoint
    */
   public String sanitizeRequestMappingPattern(String requestMappingPattern);
}
