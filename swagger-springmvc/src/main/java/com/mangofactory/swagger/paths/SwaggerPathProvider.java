package com.mangofactory.swagger.paths;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class SwaggerPathProvider {

    public static final String[] docs = {"api-docs"};
    /**
     * e.g the api endpoint resides at  http://myapp.com:8080/<contextPath>/api/v1/businesses
     * Should return /api/v1 - note the leading and non trailing forward slashes.
     * <p/>
     * Typically needed when your web.xml has a mapping to dispatcher servlet like:
     * <url-pattern>/api/v1/*</url-pattern>
     * <p/>
     * and the actual spring request mappings look like:
     * '@RequestMapping(value = "/businesses/{businessId}")'
     *
     * @return the resource prefix of the api endpoint
     */
    private String apiResourcePrefix = "";

    /**
     * return the absolute path to the app/web server hosting  the swagger documentation
     * swagger ui doesn't handle relative uri's correctly plus swagger code gen needs absolute uris
     * e.g. http://127.0.0.1:8080
     * https://mywebserver.com
     *
     * @return the applications base uri
     */
    public String getApplicationBasePath() {
        return applicationPath();

        //TODO AK - replace with this and replace all code in other classes that appends apiResourcePrefix
//        UriComponentsBuilder uriComponentsBuilder = agnosticUriComponentBuilder(applicationPath());
//        return uriComponentsBuilder.pathSegment(apiResourcePrefix).build().toString();
    }

    public String getDocumentationBasePath(String ... segments) {
        String[] pathSegments = (String[]) ArrayUtils.addAll(docs, segments);
        UriComponentsBuilder uriComponentsBuilder = agnosticUriComponentBuilder(applicationPath());

        return uriComponentsBuilder.pathSegment(pathSegments)
                .build()
                .toString();
    }

    private UriComponentsBuilder agnosticUriComponentBuilder(String appUrl) {
        UriComponentsBuilder uriComponentsBuilder;
        if (appUrl.startsWith("http")) {
            uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(appUrl);
        } else {
            uriComponentsBuilder = UriComponentsBuilder.fromPath(appUrl);
        }
        return uriComponentsBuilder;
    }

    protected abstract String applicationPath();

    public String getApiResourcePrefix() {
        return apiResourcePrefix;
    }

    public void setApiResourcePrefix(String apiResourcePrefix) {
        this.apiResourcePrefix = apiResourcePrefix;
    }
}


