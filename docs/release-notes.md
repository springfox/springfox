# 2.3.1 Release Notes

[Full Changelog](https://github.com/springfox/springfox/compare/2.3.0...HEAD)
Big thank you to  @wxjlibra, @abaile33 for reporting bugs and Thanks @sbuettner and @vmarusic for the PRs!

**Maintenance:**

- Remove runtime dependency on `spring-hateoas` #1104 
- Fix the accidental reference to `java.util.Objects` class so that java 6 is still supported
- Upgraded gradle to 2.10
- Upgraded to swagger 1.5.5
- Fix Documentation Formatting
- Upgraded jdk to 8 for building
- Upgrade Classmate to 1.3.1 #1079
- Document use of google guava in code examples #1117


**Pull Requests:**

- v2/api-docs does not support application/json media type by default  #1110
- Add support for api model property positions.  feature PR #1101 

**Bug Fixes:**

- Could not define global consumes and produces set #1115
- Support for showing controller methods with defaulted @RequestMapping#params values with different values is incorrect #1114
- Better Support for 2 dimensional arrays #956


# 2.3.0 Release notes

[Full Changelog](https://github.com/springfox/springfox/compare/2.2.2...HEAD)
As usual thank you for all your support, especially @RobWin for also hanging out on the gitter
channel and helping answering questions!

**Highlights of this release are:**
- Stable spring 3.2.x support, Spring 4.2.x compatibility and spring boot 1.3 support 
- Global operation parameter configuration
- Ability to supply examples
- Better swagger-codegen support for generating unique method names
- swagger-ui configuration options and stability
- Docket level overriding of host name

**Features:**

- Disable appending of Using\<Method\> [\#1066](https://github.com/springfox/springfox/issues/1066)
- @ApiModelProperty example field is ignored [\#998](https://github.com/springfox/springfox/issues/998)
- Configuration for global Operation-Parameters [\#845](https://github.com/springfox/springfox/issues/845)
- OAuth password grant\_type support [\#789](https://github.com/springfox/springfox/issues/789)

**Pull requests from the community:** :bow:

- Updated Swagger2Markup version to 0.9.1 [\#1065](https://github.com/springfox/springfox/pull/1065) ([RobWin](https://github.com/RobWin))
- Fix issue \#767, duplicate @JsonProperty [\#1045](https://github.com/springfox/springfox/pull/1045) ([glarfs](https://github.com/glarfs))
- Fixing logging error [\#1042](https://github.com/springfox/springfox/pull/1042) ([AndreTurgeon](https://github.com/AndreTurgeon))
- Added a fix for 'example' field in @ApiModelProperty annotation [\#1041](https://github.com/springfox/springfox/pull/1041) ([jrhowell](https://github.com/jrhowell))
- Fix 2 minor typos in the documentation [\#1013](https://github.com/springfox/springfox/pull/1013) ([erikthered](https://github.com/erikthered))
- Add host\(\) method to docket [\#1011](https://github.com/springfox/springfox/pull/1011) ([cbornet](https://github.com/cbornet))
- Springfox \#845: Configuration for global Operation-Parameters. \(Documentation\) [\#1005](https://github.com/springfox/springfox/pull/1005) ([GitVhaos](https://github.com/GitVhaos))
- fix javadoc [\#1002](https://github.com/springfox/springfox/pull/1002) ([n0mer](https://github.com/n0mer))
- Fix handling of RequestMapping that include regex with quantifier\(s\) [\#993](https://github.com/springfox/springfox/pull/993) ([erikthered](https://github.com/erikthered))
- Proper handling of @ResponseStatus with default reason\(\). [\#970](https://github.com/springfox/springfox/pull/970) ([acourtneybrown](https://github.com/acourtneybrown))
- Fix generation of "schema" section for a "body" parameter to contain correct "type" & "format" [\#968](https://github.com/springfox/springfox/pull/968) ([acourtneybrown](https://github.com/acourtneybrown))
- Allow security scheme of same type in resource listing [\#967](https://github.com/springfox/springfox/pull/967) ([cbornet](https://github.com/cbornet))
- Add ResourceOwnerPasswordCredentialsGrant and ClientCredentialsGrant [\#966](https://github.com/springfox/springfox/pull/966) ([cbornet](https://github.com/cbornet))
- DOC - Fixing path to Swagger UI image [\#932](https://github.com/springfox/springfox/pull/932) ([HNygard](https://github.com/HNygard))
- Updated swagger2markup version [\#930](https://github.com/springfox/springfox/pull/930) ([RobWin](https://github.com/RobWin))
- fix async bug [\#1078](https://github.com/springfox/springfox/pull/1078) ([rockytriton](https://github.com/rockytriton))
- Fixing typo [\#922](https://github.com/springfox/springfox/pull/922) ([phchang](https://github.com/phchang))

**Bug Fixes:**

- UiConfiguration setting validatorUrl doesn't actually work. [\#1077](https://github.com/springfox/springfox/issues/1077)
- Springfox generates empty types in the definitions \(which swagger-codegen struggles with\) [\#1063](https://github.com/springfox/springfox/issues/1063)
- ArrayIndexOutOfBoundsException when using custom Maps [\#1062](https://github.com/springfox/springfox/issues/1062)
- Swagger is not compatible with Spring 4.2.3 [\#1055](https://github.com/springfox/springfox/issues/1055)
- ClassCastException in Eclipse [\#1054](https://github.com/springfox/springfox/issues/1054)
- Provide custom resource grouping strategy [\#1039](https://github.com/springfox/springfox/issues/1039)
- @Api\(hidden = true\) does not hide [\#995](https://github.com/springfox/springfox/issues/995)
- Extra closing curly brace on endpoints defined with a regex containing a quantifier [\#991](https://github.com/springfox/springfox/issues/991)
- Multiple oauth security schemes not supported [\#959](https://github.com/springfox/springfox/issues/959)
- NullPointerException when extending controller classes with multiple parameterized types [\#953](https://github.com/springfox/springfox/issues/953)
- Regression disabling schema validator [\#951](https://github.com/springfox/springfox/issues/951)
- Sending API key to endpoints in request header [\#943](https://github.com/springfox/springfox/issues/943)
- Provide default descriptions for non-200 status codes [\#941](https://github.com/springfox/springfox/issues/941)
- General maintenance [\#939](https://github.com/springfox/springfox/issues/939)
- tags parameter in @Api is ignored by ApiOperation reader/mapper [\#934](https://github.com/springfox/springfox/issues/934)
- defaultValue parameter in RequestParam is ignored [\#933](https://github.com/springfox/springfox/issues/933)
- @ModelAttribute Generates Different Output than @RequestBody [\#929](https://github.com/springfox/springfox/issues/929)
- Change links in docs [\#928](https://github.com/springfox/springfox/issues/928)
- java.net.UUID [\#925](https://github.com/springfox/springfox/issues/925)
- Documentation not generated in latest versions of springfox with spring 3.x [\#921](https://github.com/springfox/springfox/issues/921)
- Retire ResourceGroupingStrategy in favor of the available tag support [\#919](https://github.com/springfox/springfox/issues/919)
- Support Java 8 "-parameters" [\#900](https://github.com/springfox/springfox/issues/900)
- "click to authenticate" button not clickable [\#870](https://github.com/springfox/springfox/issues/870)
- @RequestPart that are in the body aren't represented correctly [\#836](https://github.com/springfox/springfox/issues/836)
- Post release tasks [\#708](https://github.com/springfox/springfox/issues/708)
- DOC - Fixing path to Swagger UI image [\#932](https://github.com/springfox/springfox/pull/932) ([HNygard](https://github.com/HNygard))
- Updated swagger2markup version [\#930](https://github.com/springfox/springfox/pull/930) ([RobWin](https://github.com/RobWin))

**Closed issues:**

- How to override @RequestBody annotated method parameter description? [\#1069](https://github.com/springfox/springfox/issues/1069)
- Content-Type not being set [\#1029](https://github.com/springfox/springfox/issues/1029)
- Support for custom Optional class [\#1027](https://github.com/springfox/springfox/issues/1027)
- java.io.FileNotFoundException: Jar URL cannot be resolved to absolute file path  [\#1026](https://github.com/springfox/springfox/issues/1026)
- Docket documentation [\#1010](https://github.com/springfox/springfox/issues/1010)
- Everything on logs seem fine, but get 404 when I try to access the ui [\#1009](https://github.com/springfox/springfox/issues/1009)
- Method with @PathVariable Map attributes being ignored [\#999](https://github.com/springfox/springfox/issues/999)
- Swagger produce IllegalArgumentException while using Spring HATEOAS classes [\#997](https://github.com/springfox/springfox/issues/997)
- Are query parameter templates for "path" entries valid? [\#986](https://github.com/springfox/springfox/issues/986)
- Want to convert Springfox-2.0.1 source into Eclipse Maven project [\#974](https://github.com/springfox/springfox/issues/974)
- @ApiResponse maps to wrong ApiModel-definition name using "globalResponseMessage" [\#961](https://github.com/springfox/springfox/issues/961)
- Hiding injected Spring parameters from the API [\#960](https://github.com/springfox/springfox/issues/960)
- How to hide a property in an @ApiModel? [\#955](https://github.com/springfox/springfox/issues/955)
- springfox-swagger2 dependency issue [\#946](https://github.com/springfox/springfox/issues/946)
- SpringMvc error [\#940](https://github.com/springfox/springfox/issues/940)

# 2.2.0 Release notes
Thank you as usual for everyone who contributed in some for or the other to make this product better!! This release 
sneaks in some features we're incubating to provide hypermedia support.

### Features

- #866 Missing REST API descriptions in JSON @EranIsraeli
- #861 Springfox #845: Configuration for global Operation-Parameters @GitVhaos
- #834 OAUTH problem @dorukokan
- #801 Allowable Values for enumerations on response models do not render options @GitVhaos
- #766 springfox-swagger2 doesn't respect ApiModelProperty#position @olkulyk
- #755 o.s.data.domain.Pageable - automatically add @ApiImplicitParams? @steve-oakey
- #750 Same RequestMapping URL's containing different RequestParam gets overridden @rajeshkamal  
- #723 Models annotated with static JsonCreator factory methods do not render properly @who
- #679 Reuse documentation across methods/types @hestad
- #675 Tags in @ApiOperation are not rendered @who
- #590 swagger2asciidoc converter @RobWin
- #293 ModelProperty doesn't have a field for Min/Max @Charlie-IAS
 
### Bug fixes 
- #911 Documentation generate empty model when input is an interface but not always @Richou
- #905 Uncaught TypeError: Cannot read property 'clientAuthorizations' of null @mongofish
- #903 fix for issue #870. "click to authenticate" button not clickable @sloppycoder
- #901 SpringFox no more compatible with Spring 3.2.x ? @Abdennebi  2.2.0
- #897 Does springfox-swagger2:2.1.2 work with Scala? @cer
- #885 Issue Related to #586 - Empty properties when using @JsonProperty but not using @JsonCreator @jreckner
- #873 @ApiModelProperty hidden not working @ wind57
- #737 Api annotation value should remain as-is @RobWin 

#### Questions
- #916 Error while deploying on cloud foundry -::0 can't find referenced pointcut model @brajput24  
- #915 ignored values in annotations @iiyam  
- #910 Support for Spring Boot Management endpoints @sbuettner  
- #908 Response 200 always added to POST / DELETE @tatabatyo
- #902 @ApiImplicitParam: "array" dataType is getting resolved into "type":"ref" @anny-ts
- #896 How to hide a parameter/field from an object annotated with @ModelAttribute @jmarinco
- #893 Docket.ignoredParameterTypes() removes classes completly @thomseno  
- #892 Error at Spring Boot when testing @Ivan-Masli-GDP
- #889 Can I set some flag to use the non minified swagger ui for debugging? has-workaround @mrgreen7  
- #874 springfox-staticdocs usage @EranIsraeli
- #872 Range not respected by swagger ui for validation @wind57
- #795 New to Springfox @Cpruce
- #914 Does swagger2 have any support for java.time introduced in java8 can-use-for-docs @zhukboh
- #912 @BeanParam-type request parameters aren't handled properly @ejain
- #909 Bad URL when using "try it out!" swagger ui functionality with RequestParam (query param) @Fabrice-Deshayes-aka-Xtream  
- #907 @ApiModelProperty(allowableValues = "range[min, max]") not working @pn279j  
- #899 Swagger 2.1.2 deployment Error - Error creating bean with name 'apiDescriptionReader' @GLE81  
- #898 Is there a way to load my own ApiModelProperty annotation? @awenblue  
- #887 Question regarding query params @rcruzper  
- #882 I cannot get "definitions object" generated @lelininkas  
- #880 2.1.0 - nickname not getting honored as operationId @rajeshkamal  
- #877 Swagger Annotations Not Working, Using Springfox not-reproducable @samillm  
- #187 Custom Schemas for Models @anthonyroach

# 2.1.2 Release notes
This release is mostly a stabilization release due to issues related to the introduction of using the spring caching abstraction.

## Bugfixes
- #865 Weird behavior in version 2.1.1 due to RequestMapping value being named "rates" **bug** @EranIsraeli 
- #864 api.getOperations returns null **bug** @EranIsraeli 
- #863 Allow to toggle cache feature on / off *question/no longer applicable* @EranIsraeli
- #858 Caching is not working for non-trivial use cases bug. Many thanks to @cbornet @cabbonizio @rkaltreider @kevinconaway for helping identifying problems and helping with the fix!
- #851 Sending API key to endpoints in request header bug @jgraniero. Thanks @simon-ras for idenitifying the cause!

# 2.1.1 Release notes

## Bugfixes
- #855 and #856 - Fixes regression causing problems when loading dockets with group name not specified. Thanks 
@rkaltreider @cabbonizio @kevinconaway @cbornet for reporting it!

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
- #849 #801 #810 Support for enums in response values and in schema objects **feature** @kevinconaway
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
