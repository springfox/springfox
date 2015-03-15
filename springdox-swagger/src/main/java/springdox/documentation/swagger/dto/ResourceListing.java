package springdox.documentation.swagger.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ResourceListing {
  private String apiVersion;
  private String swaggerVersion;
  private List<ApiListingReference> apis;
  private LinkedHashMap<String, AuthorizationType> authorizations;
  private ApiInfo info;

  public ResourceListing() {
  }

  public ResourceListing(String apiVersion, String swaggerVersion, List<ApiListingReference> apis, List
          <AuthorizationType> authorizations, ApiInfo info) {
    this.apiVersion = apiVersion;
    this.swaggerVersion = swaggerVersion;
    this.apis = apis;
    this.authorizations = initializeAuthTypes(authorizations);
    this.info = info;
  }

  private LinkedHashMap<String, AuthorizationType> initializeAuthTypes(List<AuthorizationType> authorizationTypes) {
    if (null != authorizationTypes) {
      LinkedHashMap<String, AuthorizationType> map = new LinkedHashMap<String, AuthorizationType>();
      for (AuthorizationType authorizationType : authorizationTypes) {
        map.put(authorizationType.getType(), authorizationType);
      }
      return map;
    }
    return null;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public String getSwaggerVersion() {
    return swaggerVersion;
  }

  public void setSwaggerVersion(String swaggerVersion) {
    this.swaggerVersion = swaggerVersion;
  }

  public List<ApiListingReference> getApis() {
    return apis;
  }

  public void setApis(List<ApiListingReference> apis) {
    this.apis = apis;
  }

  public List<AuthorizationType> getAuthorizations() {
    return new ArrayList<AuthorizationType>(authorizations.values());
  }

  public void setAuthorizations(List<AuthorizationType> authorizations) {
    this.authorizations = initializeAuthTypes(authorizations);
  }

  public ApiInfo getInfo() {
    return info;
  }

  public void setInfo(ApiInfo info) {
    this.info = info;
  }
}
