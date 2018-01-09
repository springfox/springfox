##### springfox-swagger-ui

- Creates a webjar containing the swagger-ui static content.
- Adds a JSON endpoint `/swagger-resources` which lists all of the swagger resources and versions configured for a given 
application.
 
Prior versions of this library included sdoc.jsp which caused all kinds of problems on 
spring boot. This latest version now bundles a html (swagger-ui.html) instead.

The swagger ui version is specified in ./build.gradle where `swaggerUiVersion` is a git tag on the [swagger-ui repo]
(https://github.com/swagger-api/swagger-ui).
 
- All content is served from a webjar convention, relative url taking the following form: 
`webjars/${project.name}/${project.version}` e.g: `/webjars/springfox-swagger-ui/<YOUR-SPRINGFOX-VERSION>/swagger-ui.html`

By default Spring Boot has sensible defaults for serving content from webjars. To configure vanilla spring web mvc apps to serve
 webjar content see the [webjar documentation] (http://www.webjars.org/documentation#springmvc) 
