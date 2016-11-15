# 2.6.1 Release Notes

### Pull Requests
=================
- (#1546) Add native support for jdk8 jsr310 date types  @cbornet 
- (#1529) Suggested fix for "Adding Models using ApiImplicitParam #468". Allow…  @jimhooker2002 
- (#1517) Added bean validation support for @DecimalMin/@DecimalMax and made the min/max values explicit  @jamesbassett 
- (#1515) Added conditional PropertyPlaceholderConfigurer bean @dilipkrish
- (#1341) Adding @Past/@Future support to Bean Validators @boeboe 

### Features
============
- (#1535) Class Support for @ApiImplicitParam @jimhooker2002 
- (#1523) Make Swagger UI XmlHttpRequest timeout configurable?  @neildcruz19 
- (#1521) Remove defaults for min / max in MinMaxAnnotationPlugin @jamesbassett 

### Bug Fixes
=============
- (#1531) UI configuration problem when not using jackson @yangyang0507
- (#1562) IndexOutOfBoundsException in ModelMapper @lhanson 
- (#1558)  Coercing Optional<DateTimeZone> to String @kevinm416 
- (#1555) Dead circulation @evencht 
- (#1554) spec violation: springfox generates json without parameter type for @RequestParam Map<String, String> *wontfix* @nikit
- (#1553) After updating to 2.6, stack overflow exception is occured @HanDDol 
- (#1550) Custom endpoint not working @raderio 
- (#1542) @EnableSwagger2 forces microservice to register as UNKNOWN to registry @martin3361 
- (#1540) ApiImplicitParams documentation lacks Model and Model Schema for user-defined types @richmeyer7 
- (#1538) SpringDataRestConfiguration ModelAttributeParameterExpander StackOverflowError @mpostelnicu 
- (#1532) SpringFox and Eureka failing @sundarsy 
- (#1528) The service does not work after using Swagger 2.6 in Spring Cloud @puras 
- (#1525) 2.6.0 breaks the swagger documentation link @apixandru 
- (#1514) Properties are not read anymore (regression) @cbornet 
- (#1513) Respect ApiModelProperty(hidden=true) on @ModelAttribute annotated models, Cycles in Java classes cause infinite loop *has-workaround* @leelance 
- (#1508) Update overrides the default behavior for the PropertyPlaceholderHelper @apixandru


# 2.6.0 Release Notes
The one spring data rest support lands!

#### PR's 

Thank you for all your contributions!
- (#1498) Add pathsGroupedBy configuration of Swagger2Markup @orevial 
- (#1492) Intermediate push @davidnewcomb 
- (#1486) remove duplicate enum values @apixandru 
- (#1483) Update common-problems.adoc @qwang1990 
- (#1477) Initial support for spring-data-rest @dilipkrish 
- (#1476) Fix mistype in documentation @mvpotter 
- (#1474) Merge method-level and class-level @ApiResponses annotations.  @sworisbreathing 
- (#1470) Supporting for hidden attribute of @ApiParam and @ApiModelProperty annotations.  @MaksimOrlov 
- (#1469) Fixing java.sql.Date to be a date in swagger instead of date-time @mtalbot 
- (#1463) Add 'example' in merged model @wssccc 
- (#1456) java.sql.Date should become a "date" in JSON Schema output, according to docs @ssevertson 
- (#1448) Update to use swagger2markup version 1.0+ @ssevertson 
- (#1445) upgrade to swagger-ui 2.2.0 @kevinchabreck 
- (#1388) S3 maven repo @jongo593 
- (#1386) #1373 - Modified how to set jsonEditor parameter @miborra 
- (#1350) Complex type support by @ModelAttribute @MaksimOrlov 

#### Features

- (#1494) Define a reason for applying @ApiIgnore @gdrouet 
- (#1465) Type-level @ApiResponses are ignored looking-for-contributions @sworisbreathing 
- (#1444) Support for rendering parameters and model attributes when no annotations are present @tianchengbaihe 
- (#1412) Mixing string and Properties parameters causes display error in 2.5.0 has-workaround @rherrick 
- (#1395) Swagger ui dose not work when @RequestParam annotation dose not set to method parameter @azizkhani 
- (#1335) Ability to add custom ApiDescriptions not described via request mappings @romanwozniak 
- (#1294) Utilize the jackson property definition to determine additional model information @dilipkrish 
- (#1286) Make 'supportedSubmitMethods' configurable to enable and disable the "Try it out!" functionality @mibeumer 
- (#1271) @ResponseHeader description not generated in apidocs @basvanstratum 
- (#1137) Support VendorExtensions described as swagger schema @MinosPong 
- (#1021) Add @JsonFormat support for date and time @YLombardi 
- (#699) Integration with Spring-Data-Rest @raranzueque 

#### Bug fixes

- (#1504) "infinity" in allowableValues property of @ApiModelProperty annotation @SomeoneToIgnore 
- (#1485) Remove duplicate values in enum displays (case insensitive) @apixandru 
- (#1475) Map<LocalDateTime, List<String>> not displayed correctly @jlaugesen 
- (#1440) Springfox expects _links to be array, while Spring hateoas return _links as object @jiangchuan1220 
- (#1436) Explicit value JsonProperty ignored when PropertyNamingStrategy is configured _not-reproducable_ @mborkunov 
- (#1420) Result of adding tags to docket in swagger configuration @ChrisHartman 
- (#1380) ApiResponses can not support custom status code such like 1010 @dockerlet 
- (#1361) Request header "Content-Type" is not being sent with request _wontfix_ @manish2aug 
- (#1353) RequestParam with a map crashes swagger-ui wontfix @BryceMehring 
- (#1346) @ApiIgnore is not respected on a method (2.5.0-SNAPSHOT) @ben

# 2.5.0 Release Notes

#### Features

- (#1296) Support for JSR-303: @Pattern annotation @ashutosh-shirole  
- (#1291) Make 'supportedSubmitMethods' configurable in springfox-swagger-ui @thomseno  
- (#1287) Feature: Headers in @RequestMapping are not documented @ry4n-sc0tt  
- (#1244) @ApiParam - Allowable values not displayed in Swagger API docs @jfiala  

#### Bugs

- (#1325) Missing @RequestParam on a boolean parameter causes Swagger page to not render that controller, and all controllers alphabetically after __has-workaround__ @Thunderforge  
- (#1321)	Cannot fully change swagger and swagger ui path __has-workaround__ @nikit-cpp  
- (#1133) Respect ApiModelProperty(hidden=true) on @ModelAttribute annotated models __has-workaround__ @cm325  
- (#1317)	@ApiModelProperty(value="something") on bean annotated with @ModelAttribute changes dataType to "ref" @mpostelnicu  
- (#1310)	'enum' query parameter type(annotated with @ModelAttribute) is 'ref'.  @namkee  
- (#1306)	Maps as parameters were not rendered correctly @aqlu  
- (#1285)	dataType="file" is not working in springfox 2.4.0 @HDBandit  
- (#1282)	Missing model definitions in swagger json document when return type is array syntax (CustomModel[]) @namkee  
- (#1280)	@ApiModelProperty.position not respected @marceloverdijk  
- (#1268)	Compatible issue encountered with Spring Boot 1.4.0.M2 and Spring 4.3.RC @hantsy  
- (#1260)	Not working proper with query params not-reproducable @Gaurav-Deshkar  
- (#1258)	Fields not visible from children when implementing interfaces in parent request objects @stashthecode    2 of 2
- (#1249)	Bug: Templated url are submited with the template part @anthofo  
- (#1241)	Overloaded method resolution was incorrect causing ArrayIndexOutOfBoundsException @tdeverdiere  
- (#1238)	Swagger UI showing incorrect URLs @martin3361  
- (#1211)	ApiParam allowableValues string with spaces @pvpkiran  
- (#1209)	Query parameters - complex data types are coerced to 'string' type, especially collections @jkasmann  
- (#1207)	CachingModelPropertiesProvider - NullPointerException @bharatkaushik  
- (#1203)	swagger-ui location /configuration/ui configurable?  @flexguse
- (#1196)	Springfox not emitting attributes for definitions with some kinds of circular references @benfowler  

#### PRs 

- (#1331) Fix for #1282: Array dependent types not part of models  @namkee  
- (#1319) Fix for #1318: fixing favicon images urls  @rubencepeda  
- (#1316) Fix for #1296: Adding Pattern annotation support  @ashutosh-shirole  
- (#1314) Fix for #1296: Support for JSR-303: @Pattern annotation  @igor-sokolov  
- (#1233) Fix for #1207: Skip events from child application context @praveen12bnitt  
- (#1295) Make 'supportedSubmitMethods' configurable in springfox-swagger-ui / Enable and disable the "Try it out!" functionality  @thomseno  
- (#1292) Added support for documenting headers in @RequestMapping  @ry4n-sc0tt  
- (#1284) Fix for broken contributing link in README  @ry4n-sc0tt  
- (#1277) Set encoding to utf-8 for multilanguage support.  @catinred2  
- (#1275) Added message about starring the repository  maintenance @dilipkrish  
- (#1265) Don't include a license object when both license and licenseUrl are e…  @wgreven-ibr  
- (#1239) Fix for the context refresh ordering issue. Support for spring-cloud brixton  @dilipkrish  
- (#1218) Only encode the API key when passed in URL query  @franklloydteh  
- (#1217) adds basePath from x-forwarded-prefix for reverse proxy scenarios  @matonthecat  
- (#1213) Fixed some wrong asciidoc typos  @RobWin  

#### Maintenance

- (#1332) Support for jdk 8 build @dilipkrish  
- (#1326) Simplify the gradle build @dilipkrish  
- (#1322) Copyright check for license should allow for newer years @dilipkrish  
- (#1276) character encoding error in springfox-staticdocs @catinred2  
- (#1275) Added message about starring the repository  PR @dilipkrish  
- (#1253) BeanPropertyDefinitions not compatible with Jackson 2.7.x @Stephan202  
- (#1162) java.sql.Date creates an useless "Date" definition @cbornet  

Not to mention all the [questions and suggestions](https://github.com/springfox/springfox/issues?utf8=✓&q=milestone%3A2.5.0+label%3Aquestion+is%3Aclosed+) by the community!! :metal: 

# 2.4.0 Release Notes

[Full Changelog](https://github.com/springfox/springfox/compare/2.4.0...2.3.1)

#### Features
(#1145) Generated file is not compatible with Swagger specification if method parameter is Object and is used as path parameter @tjuchniewicz
(#1122)	Default page served isn't configurable and lacks search / listAll functionality @porcoesphino
(#1087) Change hardcoded "api_key" @JulianaRed
(#1061) Add support @RequestParam of type Map<String, String> @matias2681
(#1046) Control over swagger contact object @smwurster
(#1037) Add support for multiple allowable values @smwurster
(#1023) Provide a method for Model Properties to by sorted by configurable methods @nickpanaiotov
(#969) Support alternate type resolution for @ModelAttribute annotated model fields @ejain
(#937) Add support for @ResponseHeader @MarkVanVenrooij
(#936) Add support for adding global tags to the docket @dilipkrish
(#735) Support for adding additional models for request or response that are not inferred from operationshas-workaround @cabbonizio
(#388) @ApiParam not working on Interface method declarations @charleslieferando
(#356) Support for JSR-303 (Java Bean Validation) @omayevskiy

#### Bugs
(#1194) Swagger-ui does not render correctly in safari
@dilipkrish 
(#1193) NPE when Feign, Swagger and Spring Security are used - Brixton.BUILD-SNAPSHOT @varghgeorge 
(#1186) Unwanted class with map of map attribute @cbornet 
(#1174) Doc: includePatterns does not exist (anymore?) can-use-for-docs @vorburger 
(#1147) Using parameterized types using Void resulting in invalid Swagger @nigelsim 
(#1132) Api operations on abstract superclass not affected by @API tags @gionn 
(#1129) Swagger2Controller.getDocumentation get IndexOutOfBoundsException @yqzhan2014
(#1127) Setup base URL after Swagger UI is initialized #1126   @chornyi
(#1126) Race condition and crash on Swagger UI startup @chornyi
(#1125) Service description has no api's as the path regular expression does not match any of the service @irfandawood
(#1051) Customised ObjectMapper not recognized @milanov
(#953) NullPointerException when extending controller classes with multiple parameterized types @woemler 
(#902) @ApiImplicitParam: "array" dataType is getting resolved into "type":"ref" @anny-ts

#### Maintenance
(#1180) Supply the text values for `@ApiOperation, @ApiResponse, @ApiParam, @ApiModelProperty from an external resource file, instead of hardcoding?  @joetconcur
(#1168) Changelog not being updated question @jayanderson
(#1161) Update documentation for JSR310 and Joda dates @cbornet
(#1142) Fix the reference document not showing versions correctly in the gh pages @dilipkrish
(#854) Rework the ModelRef design to not be a hacky project of the swagger models @dilipkrish

#### PRs in this release!
(#1189) Allow extension of ApiResourceController by making its methods public @gmarziou
(#1165)	ParameterDefaultReader in swagger does not honor other annotations usage of DefaultValue  @ctruzzi
(#1163) Updated the Docket XML Configuration Documentation  @kellydavid
(#1159) Add basic Bean validation api (JSR-303) support  @jfiala
(#1127) Setup base URL after Swagger UI is initialized #1126  @chornyi

===================
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

# Pre 2.0 Release Notes

[1.0.2](https://github.com/springfox/springfox/issues?q=milestone%3A1.0.2)
==================================================================================
- [#625] How do I specify api version? bug
- [#623] @ApiImplicitParam always adds enums in json in version 1.0.1 bug
- [#617] Java codegen - generates class name Void.java bug 

[1.0.1](https://github.com/martypitt/swagger-springmvc/issues?q=milestone%3A1.0.1)
==================================================================================
- [#618] Ranges are treated like enums bug
- [#522] @Unwrapped types have problems sometimes... bug
- [#482] Custom ObjectMapper not recognized by swagger-springmvc bug

[1.0.0](https://github.com/martypitt/swagger-springmvc/issues?q=milestone%3A1.0.0)
==================================================================================
- [#593] Container types in model properties are now rendered as "arrays" 
- [#589] Add support for @RequestPart annotation
- [#560] Having a model property of type Class doesn't work as expected
- [#554] @ApiModelProperty "hidden" attribute has no effect

[0.9.5](https://github.com/martypitt/swagger-springmvc/issues?q=milestone%3A0.9.5)
==================================================================================
- [#552] Enable ApiResponses annotation on interface
- [#541] Fix the rendering of enums in model properties. Attempt to clean up logic to handle bare enums in the response
- [#539] Prevents excludeAnnotations from modifying the defaults in SpringSwaggerConfig
- [#496] Upgerades swagger-ui webjar version. Adds jstl dependencies to fix webjar context path

- Use @ApiModel.value as alternate type name for serialization
- Respect original property ordering on models.
- Introduced pmd and addressed the violations and added findbugs plugin
- This solution lets delegates the choice of interpreting the position. Part of the solution to the problem reported in #519


[0.9.4](https://github.com/martypitt/swagger-springmvc/issues?q=milestone%3A0.9.4)
===========================================
## Highlights
- Removed all transitive dependencies on swagger-core and scala which reduces dependency size by ~30Mb
- Provided default model substitutes for spring's ResponseEntity and HttpEntity
- Upgraded to Jackson 2.4.4
- Fixed an issue where 'body' parameters were not being named as 'body'
- Ability to disable default response messages `springfox.swagger.plugins.SwaggerSpringMvcPlugin
.useDefaultResponseMessages`

## Breaking changes
- All dependencies on swagger-core's scala models (`com.wordnik.swagger.model`) have been removed.
The equivalent java models now live in `springfox.swagger.models.dto`. If an application has
configured swagger auth or custom response messages it's likely there will be compilation issues with this
upgrade, the fix is to simply import the java equivalents from `springfox.swagger.models.dto`.
There are also builders in `springfox.swagger.models.dto.builder`. These are the most likely offenders:
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

