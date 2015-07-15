# 2.1.0 Release Notes

## Significant changes
- Caching and performance improvements. Details [available here](http://springfox.github.io/springfox/docs/snapshot/#caching)
- Added support for [RFC #6570](https://tools.ietf.org/html/rfc6570)
- Added new apis annotated with `@Incubation` and `@Deprecated` certain apis.

### Deprecations
[ResourceGroupingStrategy](https://github.com/springfox/springfox/blob/master/springfox-spi/src/main/java/springfox/documentation/spi/service/ResourceGroupingStrategy.java) has following deprecations [getResourceDescription](https://github.com/springfox/springfox/blob/master/springfox-spi/src/main/java/springfox/documentation/spi/service/ResourceGroupingStrategy.java#L54-L55) and [getResourcePosition](https://github.com/springfox/springfox/blob/master/springfox-spi/src/main/java/springfox/documentation/spi/service/ResourceGroupingStrategy.java#L66-L67)

### Incubating features
- [Docket#enableUrlTemplating](https://github.com/springfox/springfox/blob/master/springfox-spring-web/src/main/java/springfox/documentation/spring/web/plugins/Docket.java#L365-L379)
- [PathDecorator](https://github.com/springfox/springfox/blob/master/springfox-spi/src/main/java/springfox/documentation/service/PathDecorator.java) interface that takes in a [PathContext](https://github.com/springfox/springfox/blob/master/springfox-spi/src/main/java/springfox/documentation/spi/service/contexts/PathContext.java)

## Enhancements
- #831 Changes to allow for readOnly property in swagger 2  **feature** @jfearon
- #832 Add support for vendor extensions in operations  **feature** @cbornet
- #843 Swagger Resources with different base paths **feature** @samillm

## Bug fixes and maintenance
- #847 Link to minified swagger-ui @ejain
- #682 operationId is not guaranteed to be unique **bug**
- #711 Support for multiple request/response models based on different query string parameters and also accept headers **bug**
- #771 Duplicate Tags when multiple @Api have same value. **bug** @rkaltreider
- #817 Maps of maps are not correctly handled **bug** @cbornet
- #825 Generated spec is non-deterministic - different runs different outputs **bug** @rajeshkamal
- #829 o2c.html not found **bug** @yukinami
- #830 Newest version of swagger2markup  **maintenance** @RobWin
- #833 initOAuth in swagger-ui.html is not invoked. **bug** @yukinami
- #837 Array of multipart file parameters are not rendered correctly **bug** 

## Questions and Documentation
- #821 Configuring authorization **can-use-for-docs question** @poliveiraTDsis
- #827 missing documentation **can-use-for-docs** @rcruzper
- #828 Using Maps **question** @rcruzper
- #835 @ApiResponses is suppressing "Response Content Type" drop down **can-use-for-docs question ** @dpatra1
- #839 Swagger Spring MVC missing controller names **question won't fix** @samillm 
- #822 Response code in Response Entity ignored **can-use-for-docs question** @jfearon
- #840 overflow-y **can-use-for-docs question** @wind57

# 2.0.3 Release notes
Includes major bug fix that caused degraded performance and a few minor bug fixes

- #806 Improve performance of model processing maintenance (Thanks @RizziCR and @RobWin for reporting)
    - #811 Slow Startup - Spring Boot @bryantp
    - #812 swagger springfox unable to initialize when moving from 2.0.1 to >2.0.2 @roya2 
- #805 ApiOperation response doesn't work Thanks! @EdwardsBean
- #813 Duplicate Params - Swagger Spec and in Generated Code Thanks! @rajeshkamal  2.0.3
- #803 [Documentation] Added note on @EnableWebMvc conflict when using Spring Boot Thanks! @igilham
- #804 CircleCI no longer publishes the snapshot builds bug maintenance

# 2.0.2 Release notes
Significant changes include:
- Adjust namespaces due to a change in package names in swagger-core maintenance. Swagger-Core 1.5 release changed the package names from `com.wordnik.swagger.*` to `io.swagger.*`
- Improved the swagger-ui integration 

# Contributions
Thank you for all your contributions!

- #796 Property to disable Schema-Validator (Swagger-UI) *feature* - @GitVhaos
- #793 @ApiResponse maps to wrong ApiModel-definition name *bug* - @GitVhaos
- #788 Why is the initOAuth function commented? *bug* - @rmarpozo
- #787 Bring in @RequestPart annotation support  *bug* - @ammmze
- #786 @RequestPart with @ApiParam not rendering a definition in 2.0.0 *bug* - @mrisney
- #785 Updated swagger2markup version  *maintenance* - @RobWin
- #781 CORS error message displayed at top of the page *bug* - @gmarziou
- #778 @RequestParam Field With Default Value Marked Required *bug* - @kevinconaway
- #776 swagger-ui endpoint doesn't seem to work can-use-for-docs *question* - @igilham
- #775 Swagger 2 - MultipartFile not detect/mapped correctly *bug* - @RizziCR
- #774 Newest version of swagger2markup  *maintenance* - @RobWin
- #773 Swagger 2.0 - Cannot Have Blank Notes / Implementation Details has-workaround *maintenance* - @kevinconaway
- #768 MultiPartFile Request Parameters are being incorrectly typed as type "ref" *bug* - @rince1013
- #752 Groovy metaClass not ignored when model is built for deserialization *bug can-use-for-docs* -  @aleksz

# 2.0.1 Release Notes
- #759 [maintenance] Improve the build workflow 
- #754 [maintenance] Provide necessary jars in maven central 
- #726 [maintenance] Setter overloading with different Type of param will trigger IllegalArgumentException (thanks  @gaplo917)
- #734 [feature] @RequestBody(required = true) does not render required params (thanks @who) 
- #664 [feature] Array[enum] parsed as Array[String] in request feature (thanks @hestad)
- #747 [bug] Problems using Map<String, String> requests in responses (thanks @akurdyukov)
- #740 [bug] List<Map<String, String>> in models not rendered correctly (thanks @nyddogghr)
- #733 [bug] ApiResponses cannot be customized/overridden (thanks @who) 
- #728 [bug] Request the swagger JSON will throw java.lang.NullPointerException (thanks @gaplo917)
- #727 [bug] ApiListingScanner doesn't work correctly for 2 ResourceGroups with the same name (thanks @dplacinta)
- #717 [bug] Multiple controllers containing same parts of the URL gets missed/overridden in the spec (thanks  @rajeshkamal)
- #713 [bug] Swagger UI page with operations open: browser page refresh opens Swagger Petstore operations (thanks   @keesvandieren)
- #707 [bug] Controller bean class matters in Resource Group (Java) (thanks @HiPwrD64) 
- #702 [bug] @ApiResponses 2.0.0 Snapshot - not rendering - disabled useDefaultResponseMessages (thanks @rajeshkamal)
- #688 [bug] Parameter Data Type does not print (thanks @pprabhu3430) 

# 2.0.0 Release notes
This is a major release for springfox (formally swagger-springmvc). This release includes the long awaited support for Swagger 2.0 along 
with some significant architectural changes aimed at improving extensibility and laying a foundation for sporting API 
specifications other than Swagger.
 
There has also been some less visible work going on:
- Moving to the Springfox Github organisation.
- Moving to a new [Bintray organisation](https://bintray.com/springfox/).
- A new Sonatype OSSRH Group, 'io.springfox'
- Moved CI to CircleCi
- Using [Asciidoctor](http://asciidoctor.org/) to generate reference documentation
- Release automation.

## Breaking changes
- All artifacts now have the organisation 'io.springfox' not 'com.mangofactory' 
- All classes now have a toplevel namespace of 'springfox', 'com.mangofactory' no longer exists.
- `springfox.documentation.spring.web.plugins.Docket` replaces what was `com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin`
- `com.mangofactory.swagger.configuration.SpringSwaggerConfig` has been removed.

## New Features
- Support for Swagger 2.0.
- The swagger-ui webjar no longer requires a JSP engine.
- Powerful ways to include or exclude API endpoints using `springfox.documentation.spring.web.plugins.ApiSelectorBuilder` 

## Contributors
We would like to thank the following community members for helping with this release:
- [https://github.com/Aloren](Nastya Smirnova)
    -  Very valuable testing, bug finding and suggestions 
- [https://github.com/jfiala](https://github.com/jfiala)
    - Very valuable testing and bug finding 
- [Andrew B](https://github.com/who)
    - ability to optionally populate nickname
- [sabyrzhan](https://github.com/sabyrzhan)
    - Bugfix for UnresolvablePlaceholders
- [paulprogrammer](https://github.com/paulprogrammer)
    - Fixed reference to old class name
- [John hestad](https://github.com/hestad)
    - Updated documentation
- [jordanjennings](https://github.com/jordanjennings)
    - Updated documentation
- [Tony Tam](https://github.com/fehguy)
    - Updated Swagger Link and added springfox/swagger editor example
- [sashevsky](https://github.com/sashevsky)
    - Fixed an issue with missing http port
- [Robert Winkler](https://github.com/RobWin)
   - Generate asciidocs from springfox swagger [687](https://github.com/springfox/springfox/pull/687)
