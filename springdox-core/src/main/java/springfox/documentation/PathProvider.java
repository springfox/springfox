package springfox.documentation;

public interface PathProvider {
  String getApplicationBasePath();

  String getOperationPath(String operationPath);

  String getResourceListingPath(String groupName, String apiDeclaration);
}
