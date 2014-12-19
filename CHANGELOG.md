## Change Logs

[0.9.4](https://github.com/martypitt/swagger-springmvc/issues?q=milestone%3A0.9.4)
===========================================
## Highlights
- Removed all transitive dependencies on scala which reduces dependency size by ~30Mb
- Provided default model substitutes for spring's ResponseEntity and HttpEntity
- Upgraded to Jackson 2.4.4
- Fixed an issue where 'body' parameters were not being named as 'body'
- Ability to disable default response messages `com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin
.useDefaultResponseMessages`

## Breaking changes
- All dependencies on swagger-core's scala models (`com.wordnik.swagger.model`) have been removed. The equivalent java models now live in `com.mangofactory.swagger.models.dto`. If an application has configured swagger auth or custom response messages it's likely there will be compilation issues with this upgrade, the fix is to simply import the java equivalents from `com.mangofactory.swagger.models.dto`. There are also builders in `com.mangofactory.swagger.models.dto.builder`. These are the most likely offenders:
   - com.wordnik.swagger.model.ApiInfo
   - com.wordnik.swagger.model.Authorization
   - com.wordnik.swagger.model.AuthorizationCodeGrant
   - com.wordnik.swagger.model.AuthorizationScope
   - com.wordnik.swagger.model.AuthorizationType
   - com.wordnik.swagger.model.GrantType
   - com.wordnik.swagger.model.ImplicitGrant
   - com.wordnik.swagger.model.LoginEndpoint
   - com.wordnik.swagger.model.OAuth
   - com.wordnik.swagger.model.OAuthBuilder
   - com.wordnik.swagger.model.TokenEndpoint
   - com.wordnik.swagger.model.TokenRequestEndpoint
   - com.wordnik.swagger.model.ResponseMessage

[0.9.3](https://github.com/martypitt/swagger-springmvc/issues?q=milestone%3A0.9.3)
===========================================

[0.9.2](https://github.com/martypitt/swagger-springmvc/issues?q=milestone%3A0.9.2)
===========================================

[0.8.8](https://github.com/martypitt/swagger-springmvc/issues?q=milestone%3A0.8.8)
===========================================

swagger-springmvc-0.8.6
===========================================
 * 8294504b1b867d39d66e5ef69306c245c1496161
Adding the posibility of ignoring an entire controller
 - Ingoring all methods of a controller by adding an excluded annotation
    on class level
 - @ApiIgnore can now be specified on Type, but also on Parameter and has
   been added to the defaultIgnorableParameterTypes Set

swagger-springmvc-0.8.5 / 2014-06-07
===========================================
 * Allows detection of customs SwaggerSpringMvcPlugins from spring context's with ancestors.

swagger-springmvc-0.8.4 / 2014-04-27
===========================================

 * Fixed an issue with the ClassOrApiAnnotationResourceGrouping that was making a cases sensitive compare
 * Autowired the ObjectMapper ...
 * Made junit a test dependency
 * Merge pull request #260 from MinosPong/master
 * Updating the notable dependencies
 * Merge remote-tracking branch 'upstream/master'
 * Fix Enum allowable list does not display in the Swagger Model
 * Updated the version dependencies
 * Merge pull request #246 from jkorri/allowables_for_enum_array_params
 * Fixed the tests as a result of fixing #255
 * Removes the duplication of controller naming strategy being set
 * Fix the rendering of void responses
 * Merge pull request #258 from lucastschmidt/master
 * #257 bug - slow scanner
 * When substituting types the base types should not be included as models
 * Include array element type for method parameters
 * Automatically retrieve allowableValues and set allowMultiple for enum arrays
 * Update readme.md
 * Released version 0.8.3
 * [maven-release-plugin] prepare for next development iteration

