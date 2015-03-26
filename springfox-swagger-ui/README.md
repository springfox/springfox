##### springfox-swagger-ui

- Downloads the swagger-ui code from github and adds its dist directory into the manifest of a jar file.
- Effectively a webjar which can be used by projects using swagger-springmvc.

The swagger ui version is controlled by the build.gradle file where `swaggerUiVersion` is the git tag of the
https://github.com/wordnik/swagger-ui repo which is packaged into the jar.
 
- Bundles a jsp file which should display the swagger UI at <host>/app/sdoc.jsp
- http://localhost:8080/sdoc.jsp
  