# 2.9.2 Release Notes
This is mostly a service release. Predominantly to publish the repository to maven along with minor improvements

## Pull requests and contributions
- (#2492) Added missing backtick   @TwinProduction
- (#2465) X-Forwarded-Prefix should replace basePath in newer spring versions   @rainoko
- (#2464) Remove replaceAll with trim   @ctruzzi
- (#2434) Add csrf token support   @olOwOlo
- (#2429) Remove output related to fixed issue (#1244) #2428   @haelduksf
- (#2404) Using media type to determine correct param type when expanding param…   @andyRokit

## Bugs
- (#2502) OperationContext only finds SecurityContext's by path ignoring methods 
@robinsonmark 
- (#2481) ConditionalOnWebApplication not found after upgrading to 2.9.0  `duplicate` @gionn
- (#2461) 2.8.0 /swagger-resource/configuration/ui does not set Accept header properly  @OverDrone
- (#2446) ApiParam allowableValues string with spaces is not supported  @uriparush
- (#2438) Upgrade to SpringFox 2.9.0 causes failures in Spring Boot 2.0.2.RELEASE  @james
- (#2428) description = "@Size: Min - Max (until #1244 gets fixed)" when param annotated with @Min and/or @Max but #1244 is already fixed  @haelduksf
- (#2423) Query Parameter extracted from Object doesn't recognize public field without getter  @kintomiko
- (#2415) JSR-303: Size annotation on Path Param generates maximum/minimum, but on Model generates maxLength/minLength  @retinaburn
- (#2401) Present multipart object attributes as "formData"  `duplicate` @andyRokit
- (#2376) Model attribute expansion should respect form/query parameter type 
@dilipkrish
- (#2498) Path Parameter generated having unsupported properties `duplicate` @FossilBlade
- (#2481) ConditionalOnWebApplication not found after upgrading to 2.9.0  `duplicate` @gionn
- (#2466) There is no jar in maven repo.Why? `duplicate` @litttlefisher
- (#2441) Upgrading from 2.8.0 to 2.9.0 causes SpringBootTest to stop working `duplicate` @maraswrona
- (#2401) Present multipart object attributes as "formData"  `duplicate` @andyRokit
- (#755) o.s.data.domain.Pageable - automatically add @ApiImplicitParams? can-use-for-docs `duplicate`  @steve

## Feature
- (#2180) Is it possible to document OAuth 2 scopes for different HTTP methods? feature @Beontra


## Maintenance
- (#2503) Upgrade libraries and patch versions  @dilipkrish
- (#2448) X-Forwarded-prefix not work as expected since spring version 4.3.15  @rainoko


# 2.9.0 Release Notes
This is the last release supporting jdk 6 and spring 4.x. The next planned release is 3.0 which will have jdk 8 as a pre-requisite. There was a large effort to ensure a stable release before moving to the next major release. A big thank you again for the numerous feature/bug requests for making this product better and supporting this library. 

A special thank you to @kasecato, @MaksimOrlov, @neumaennl, @rgoers and @Fyro-Ing for working on some much requested features.

NOTE: All deprecations will be removed when we move to 3.0

## Pull Requests
- (#2356) Add tag vendor extensions  @nhtzr 
- (#2323) fix for spring data rest 3.0.5.RELEASE while maintaining backward com…  @deodeveloper 
- (#2313) springfox-data-rest: fix EntitySearchExtractor return type  @iles
- (#2302) Fix several typos in the documentation  can-use-for-docs @rillig
- (#2288) add Bean Property Definition presence check  @bratwurzt 
- (#2277) Not checking if defaultValue on is empty  @matthewmcgarvey 
- (#2275) Take the parameter name from attribute @RequestPart value  @VasilievAleksey 
- (#2264) Upgrading swagger-ui to 3.11.0  maintenance @kasecato 
- (#2258) Issue 2257 fix default file encoding related failures  @atsu85 
- (#2228) Added minify plugin  maintenance @kasecato 
- (#2225) Added support for IE 11 and Edge  @kasecato 
- (#2163) add @SwaggerExampleObject annotation.  @gena
- (#1817) Support javadoc based document generation => Issue 1691  @rgoers 
- (#1671) General functionality for comparing models. Initial support for #182, #807, #895, #1356  @MaksimOrlov 
- (#1152) Add Polymorphism Support  @Fyro-Ing

## Features
- (#2361) Make Documentation Plugins Bootstrapper autostart configurable feature @dilipkrish 
- (#2293) Added Vendor Extensions Support to Tag object feature help wanted @nhtzr 
- (#2253) Producing "x-example" with @ApiParam for @PathVariable feature help wanted @kilsbo 
- (#2166) Swagger UI 3.x Suggested Improvements feature help wanted @dilipkrish 
- (#2125) I want to Example Value customize feature help wanted @cheese10yun 
- (#1903) XmlAttribute not applied duplicate feature @janfockaert 
- (#1151) Add Polymorphism Support feature @Fyro
- (#868) Springfox support for polymorphism duplicate feature help wanted @sunaina
- (#2043) Generate own model @deblockt 
- (#1700) @ApiModelProperty add example will be parsed to json `duplicate` @igieon 
- (#1232) Externalizing Descriptions of Api-Annotations (ApiModel/ApiModelProperty/...) @jfiala 
- (#1449) @ApiModelProperty not displaying attribute when using @ModelAttributeode `duplicate` @rantunesboreas

## Bugs
- (#2039) Endpoints with the same path and parameters but different headers some times cause java.lang.IllegalArgumentException: Multiple entries with same key  @joaoacmota 
- (#2194) Swagger-UI authorization headers stopped being sent in 2.8.0  @ctmay4 
- (#1964) How to disable 'example' output in models for booleans  @fairct 
- (#2127) Endpoints not exported with Spring Data REST  has-workaround @drenda 
- (#1827) Error when rendering recursively defined models  @tcsw1221 
- (#2230) @XMLRootElement and @XmlAttribute ignored  @rstmm 
- (#2265) @ApiModelProperty throwing NumberFormatException if example value is not set  wontfix @nikunjundhad 
- (#2204) Schema error: should NOT have additional properties allowEmptyValue  @igorko 
- (#2203) Nested maps are not being converted properly to Swagger models  @francocm 
- (#1937) @ModelAttribute with getters and no setters  @bademux 
- (#2357) swagger-ui.html in 2.8.0 contains references to 2.8.0-SNAPSHOT  @nahguam 
- (#1831) @ApiParam hidden=true does not work on @RequestHeader  help wanted @scottf 
- (#2231) Cannot create a rule to substitute list items with primitive types  @mclem 
- (#2182) JsonUnwrapped prefix ignored in release 2.7.0  @ccdd4ever 
- (#2314) UUID parameters don't have format "uuid"  @dominik
- (#2249) How to avoid the automatic number convertion for example  @thelo
- (#2368) Void type in @ModelAttribute annotated classes.  @MaksimOrlov 
- (#1944) @ApiResponse(... response = Void.class) produces empty model, (... response = void.class) does not  has-workaround @rtomsick 
- (#2076) How to convert a model Property to an Attribute in XML based req/res spring boot application?  @vinodamity 
- (#2226) JAXB is required as explicit dependency for kotlin service/  @flatiron32 
- (#2257) Tests fail on windows  @atsu85 
- (#2272) springfox-data-rest v3 support  `duplicate` @PascalSchumacher 
- (#2311) springfox-data-rest: Inconsistent model returned between findAll and search methods  @iles
- (#2322) NumberFormatException when parsing node name from Forward header with double quotes  `not-reproducable` @thiagolocatelli 
- (#2216) Springfox Swagger UI [2.8] IE / Edge Support  @gerard
- (#2278) Swagger UI 2.8.0 doesn't show response working with ApiOperation  can-use-for-docs @mhersc1 
- (#2320) SpringFox 2.8.0 Error: "Could not resolve pointer: /definitions/String"  @mrgrew 
- (#2212) SizeAnnotationPlugin spams log at INFO level  @shabino 
- (#2220) OperationParameterReader does not process optional path parameters correctly  @raphw 
- (#2235) Full URLs in /swagger-resources no longer works after 2.7.0 -> 2.8.0 Upgrade  @gloeglm 
- (#2263) @RequestPart "name/value" attribute ignored  @fschmager 
- (#2268) Optional query parameters are marked as required in the UI  `not-reproducable` @ok11 
- (#2271) When there are two return bodies with the same name class, only one class parameter can be parsed normally. For example: two Data classes  `duplicate` @k631583871 
- (#2282) java.lang.StackOverflowError happened when model extends genericity class  @Seven4X 
- (#2283) IE11 browsers can't display page on version 2.8.0  `duplicate` @903452746 
- (#2296) @RequestParam field with default value "" (empty string) is marked required  `duplicate` @Mumi
- (#2298) Support for breaking spring-data-rest changes `java.lang.NoSuchMethodError`  `duplicate` @salah3x 
- (#2309) Swagger-ui web jar is not producing the artifact correctly  @dilipkrish 
- (#2315) Swagger 2.8.0 not working on IE11  `duplicate` @Snina88 
- (#2316) @RequestPart not reporting type or allowableValues in generated API  `not-reproducable` @bgiaccio 
- (#2347) Parameter Vendor Extensions doesn't show up in generated spec  @RisenZhong 
- (#2276) @RequestParam with defaultValue of empty string on String parameter marked as required  @matthewmcgarvey 
- (#2286) [XmlPropertyPlugin]: Optional.get() cannot be called on an absent value  @bratwurzt 
- (#2370) Not able to disable schema validation.  @makcpop 
- (#1909) springfox doesn't work with Jackson 2.9.0.pr4 beihaifeiwu 
- (#2344) When i use Swagger Junit is not working not working  @pparnati 
- (#2365) The json serialiser ignores the dataType when output the digits only string @ouya2

## Maintenance
- (#2350) Upgrade libraries  @dilipkrish 
- (#2329) Activate springfox only when web environment is available  @dilipkrish 
- (#2264) Upgrading swagger-ui to 3.11.0  `PR`  @kasecato 
- (#2256) Docket SecurityContexts to filter by HTTP methods  @vadimkim 
- (#2252) Suggestion about PojoPropertyBuilderFactory: Change the alerts about Jackson 2.6 compliance  @rs
- (#2243) Remove dependency on org.reflections.Reflections  @leewinder 
- (#2228) Added minify plugin  `PR`  @kasecato 
- (#2219) ApiListingScannerPlugin is ignored if no Spring controller is registered  @raphw 
- (#2137) From swagger editor able to get the JWT auth token, but from application which enabled swagger-ui using springfox is not working  @ranjithap7576 
- (#1980) spring-data-rest RepositoryRestResource.collectionResourceRel not Mapped in Model  @n3utrino 
- (#1973) Springfox Swagger configuration breaks customized ObjectMapper in Spring Boot has-workaround  @avillev 
- (#1680) HAL `Resources` not rendered correctly `duplicate` @raffaelschmid
- (#1657) Parameter vendor extension support @zidanluo

# 2.8.0 Release Notes

## Pull Requests
- (#2178) Change regex in Paths.java to handle expressions/constraints correctly @nobe0716
- (#2174) fix fmt   maintenanc @silenceshell
- (#2169) Swagger ui 3.x suggested improvements   `feature` @kasecato
- (#2160) Fix conversion of byte to integer with max / min   `bug` @avdv
- (#2153) Upgrading Swagger UI to 3.7.0  @kasecato 
- (#2144) Allow ApiModelProperties on methods to be discovered from superclasses  @RoyJacobs 
- (#2106) Add support for exclusive ranges handling  @filiphr 
- (#2103) Fix some tests under windows  @apixandru 
- (#2101) Fix Remapping issue  @apixandru 
- (#2081) Fix child expansion context creation   `bug` @gzsombor 
- (#2069) Optimise HandlerMethodResolver.getMemberMethods  ` maintenance` @simongajdosech 
- (#2066) Added error handling around "duplicate" request handlers  @mate1983 
- (#2048) Support explicit ordering for Tags  @jroweboy 
- (#2040) Use Guava 20.0 throughout the project  maintenance @Thunderforge 
- (#2014) Fix markdown  @koppor 
- (#2013) Fixed merging headers from the already existing request with the supplied request  @pvanassen 
- (#1988) custom the web page title as swagger.title when had set it  @rainplus 
- (#1974) Add default property support on model properties   `feature` @matrosovs 
- (#1956) @ApiModelProperty example string does not escape char "\" from JSON example  @heapifyman 
- (#1952) Fix to recognize @Param as query parameter in EntitySearchExtractor know  @viruscamp 
- (#1943) Wrong API resource path in Swagger 1.2  @mathieuales 
- (#1942) Model classes having names containing integers are not detected as array  @mathieuales 
- (#1917) Consumes / Produces media-types on the document level aren't copied and merged anymore with the operation level consumes / produces media-types.  @mzeijen 
- (#1914) Provide proprty pattern annotation support  @simonamc 
- (#1897) Correct spelling and typos  @naXa777 
- (#1878) Preserve tags order in documentation builder  @rainoko 
- (#1868) Create EntitySaveExtractor.java  @jadhavsuhas 
- (#1838) Fix a mixed up part in the Getting Started guide for Docket  @PeterWippermann 
- (#1837) Minor update of Docket's JavaDoc  @PeterWippermann 
- (#1829) JacksonEnumDeterminer to handle JsonFormat.Shape.Object  @yelhouti 

## Features
- (#2177) Paths.sanitizeRequestMappingPattern fix @nobe0716 
- (#2139) Should support "title" property, set via @ApiModel annotation.  @ngbalk 
- (#2088) @ApiParam(allowableValues = "range(0, infinity)") does not work @filiphr 
- (#2063) Added support for Pageable resolved parameter @avillev 
- (#2057) Swagger-ui don't render additionalProperties `duplicate` @deblockt 
- (#2026) Produces/Consumes do not maintain order @jgaribay21 
- (#2023) Is it possible to disable globalResponseMessage configuration partially. `duplicate` @dohoon
- (#2021) @ApiModelProperty.allowEmptyValue = true/false does not emit "allowEmptyValue" in swagger.json @bill
- (#2000) Upgrade to latest version of Swagger UI (3.1.5) `duplicate` @madheshr 
- (#1960) Upgrade to swagger-ui 3.0 `duplicate` @alex
- (#1957) springfox doesn't work with spring boot 2.0 and spring data Kay-RC2 `duplicate` @shashankitmaster 
- (#1955) Add support for inclusive and exclusive ranges for allowable values @JohnNiang 
- (#1946) collectionFormat problem @vitek499 
- (#1936) configuration for adding dynamic api-key(access token value). `duplicate` @akashgupta08 
- (#1919) Add support to rename ApiModel property name in Model Attributes @peterjurkovic 
- (#1901) Pattern Bean Validations API (JSR-303) support for Request Parameters  help wanted @simonamc 
- (#1900) Tag custom ordering @rainoko 
- (#1818) JsonFormat for enum and other cases @yelhouti 
- (#1729) Status of support for v3.0.2 of Swagger UI `duplicate` @JLLeitschuh 

## Maintenance
- (#2161) How to set a default value to a field of a model? in progress @michele
- (#2093) swagger-ui.html appears to be empty `documentation`  @silentsnooc 
- (#2090) When using AlternateTypeRuleConvention ApiModelProperty annotation does not work @snimavat 
- (#2031) How to get object in response body in autogenerated swagger.json file `documentation`  @rajat
- (#2029) ApiImplicitParam with empty datatype fails when we try it out `documentation` @ljp510016132 
- (#1995) View APIs from different Spring Cloud Instances registered in Eureka `documentation`  @s
- (#1971) Vavr/Javaslang Jackson module support `documentation`  @Sir4ur0n 
- (#1954) Multiple swagger JSON's in swagger-ui.html `documentation`  @dreambrother 
- (#1950) Document support customized param using HandlerMethodArgumentResolver `documentation`  @neil4dong 
- (#1916) Consumes and produces media-types defined on Docket are incorrectly merged together with consumes/produces media-types that are defined on a resource level @mzeijen 
- (#1913) Space getting added to oAuth scope while making authorization request `documentation`  @mojaiq 
- (#1904) Nondeterministic output for Models used in multiple controllers `documentation`  @kevinm416 
- (#1899) Upgrade libraries @dilipkrish 
- (#1896) Wrong spelling and typos in code @naXa777 
- (#1882) @RepositoryRestResource -- ApiParam definition for the JPA methods always defines the @Param as "body" type parameter @aniruthmp 
- (#1875) Tags should be orderable  @rainoko 
- (#1870) The lasted version supported for Swagger UI 3.x?  @maliqiang 
- (#1865) Can't test the configuration 404 not found `documentation` @pinkyjain26 
- (#1833) Different guava versions in dependencies @Dimok74 
- (#1704) Document springfox oauth2 `documentation` `duplicate` @kidshg 

## Bugs

- (#2165) AlternateTypeRules doesn't work as expected @crmky 
- (#2148) 2.7.1-SNAPSHOT NullPointerException when attempting to view http://localhost:8080/v2/api-docs @beardy247 
- (#2138) java.util.Optional<java.time.OffsetDateTime> disappears from request params  `duplicate` @bohdan
- (#2135) No qualifying bean error when launching spring 5.0.x application with springfox  `duplicate` @gauravphoenix 
- (#2133) Optional<LocalDateTime> @kitsjory 
- (#2132) @ApiModelProperty has no effect on some variables (name starting with one lowercase)  `not-reproducable` @bbrenne 
- (#2118) Request type mapping doesn't work if using both RequestBody and ModelAttribute on the same parameter @andrea
- (#2114) @RequestParam and @PathVariable annotated parameters should not be expanded @loxal 
- (#2111) Application startup failed org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'documentationPluginsBootstrapper' @shobhit921 
- (#2107) @ApiParam ignores certain properties  `duplicate` @milosonator 
- (#2097) Swagger 2 : Getting "type": "ref" when using @RequestPart  `duplicate` @jrishabh198 
- (#2096) @ApiParam is ignored on ValueObjects wrapped with @JsonCreator @dkellenb 
- (#2080) NullPointerException in handlerPackage  `duplicate` @ejuniorasas 
- (#2072) Springfox generates Api-Doc for non-exported Repositories @stoetti 
- (#2053) @PathVariable work with Parameter Converter strangely  `duplicate` @cxl086 
- (#2039) Endpoints with the same path and parameters but different headers some times cause java.lang.IllegalArgumentException: Multiple entries with same key @joaoacmota 
- (#2025) SpringFox-Data-Rest 2.7.0: Missing camelCase with generated Associations operationIds @stoetti 
- (#2015) Add support for generics  `not-reproducable` `wontfix` @raderio 
- (#2012) Duplicated swagger.json document  `not-reproducable` @cesartl 
- (#2011) OperationBuilder mergeResponseMessages overwrites headers @pvanassen 
- (#1999) Generated JSON for endpoints with PathVariables converted to non-trivial objects is incorrect  `duplicate` @joel
- (#1998) PathVarible composed of Custom Objects are not parsed correctly  `duplicate` @frbo42 
- (#1965) @RequestPart annotation not rendering models correctly @pratapyelugula 
- (#1963) Spring Data Rest Integration doesn't document repository method parameters correctly @thombergs 
- (#1961) Springfox - Authorization value didn't update after Authorize confirmed..  @thomasharin 
- (#1941) swagger-ui does not remove java string escape char "\" from @ApiModelProperty example @heapifyman 
- (#1932) BasePathAwareController docs aren't base path aware @fquinner 
- (#1926) Can't be filtered by ignoredParameterTypes  `not-reproducable` @heyuxian 
- (#1924) Unable to define host in Swagger 1.2 @mathieuales 
- (#1906) Swagger shows wrong id field using Spring Data Rest  `duplicate` @drenda 
- (#1894) @EnableSwagger2 breaking my unit tests  `not-reproducable` @rawadrifai 
- (#1890) Can't declare Docket in multiple Configuration classes  `not-reproducable` @lorenzobenvenuti 
- (#1887) @ApiResponses tag does not override default responses  `not-reproducable` @muff1nman 
- (#1880) Wrong API resource path in Swagger 1.2 @mathieuales 
- (#1876) NullPointerException with Spring Data Rest integration @drenda 
- (#1866) 2.7.0 does not list PATCH methods from Spring Data Rest @jadhavsuhas 
- (#1864) @NotNull not working to mark field as required  @sddakoty 
- (#1860) [Spring boot] @EnableAspectJAutoProxy cause endpoint scanning failed  `duplicate` @jdupont22 
- (#1841) Overloaded method does not respect the "tag" option @jackmatt2 
- (#1839) Primary keys are added to path parameters for Spring Data Rest Entities POST request after upgrading to 2.7.0 @jadhavsuhas 
- (#1830) CORS headers disappeared after upgrading to 2.7.0  `not-reproducable` @gionn 
- (#1804) Api key input missing in swagger ui after upgrading version to 2.6.1 @prajapatkiran 
- (#1781) Swagger JSON generated by SpringFox shows 'Consumes' for GET and DELETE operations @dcp65 
- (#1672) Swagger-UI giving 405 (Method not allowed) when called  `not-reproducable` @GarrettMosier 

# 2.7.0 Release Notes

## PRs 
- (#1806) Update PropertySourcedRequestMappingHandlerMapping.java  @OzgaRobert 
- (#1793) [#1770] Remove the requirement for property sources placeholder  @dilipkrish 
- (#1782) Model property vendor extension  @philippejulien 
- (#1776) Fix Swagger version error in documentation  @ersinciftci 
- (#1761) Vendor Extensions for API Info and Documentation  @jkgentry 
- (#1758) Use double backtick to prevent miss parsing  @naxhh 
- (#1741) 1740 @JsonUnwrapped is ignored by schema generation test  @StepanLeybo 
- (#1730) Fix a typo at the common-problems documentation file  @florianrusch 
- (#1717) Adding vendorExtensions in apiKey class  @cfernandezh 
- (#1702) Make ApiResourceController methods public  @psyho 
- (#1699) Make Swagger2Controller respect basePath even if a host is not set  @asdcdow 
- (#1693) Fix for https://github.com/springfox/springfox/issues/1653  @pjskyboy 
- (#1667) #1666 allowableValues blank for Optional<Enum> parameter  @madgnome 
- (#1660) waffle.io Badge  maintenance @waffle
- (#1617) Updated swagger-ui version to 2.2.8  @acourtiol 
- (#1593) OAuth2 not initialized when clientSecret undefined  @gonzalad 
- (#1589) Apply ApiParam hidden attribute to parameters   @defshine 
- (#1576) Added support for composed bean validation constraints  @jamesbassett 
- (#1371) JSR-303 for Request parameters, Fix Allowable values not displayed in Apidocs, Externalizing Api Descriptions  @jfiala 

## Features
- (#1759) Support for property vendor extensions @philippejulien
- (#1707) Removal of "swagger-ui.html" from uri path causes swagger-ui JavaScript error @LukeHackett 
- (#1636) VendorExtension support in ApiKey @mlstocks
- (#1627) Error with group handling response @marchc 
- (#1592) OAuth2 clientSecret shouldn't be required for implicit flow @gonzalad 
- (#1590) springfox-data-rest : Pageable not supported duplicate @tooms4444 
- (#1544) JDK8 JSR310 types support @cbornet 
- (#1497) springfox-staticdocs : Update to last swagger2markup version @orevial 
- (#1490) Infer alternate type rules using serializers and deserializers @justcoon 
- (#1423) @RequestParam with placeholders syntax like ${x.y} @blelem 
- (#1413) Ability to set VendorExtensions on ApiInfo? @michael-pratt 
- (#1367) Springfox overwrites swagger path entries with the same base path but with different content types @codecounselor 
- (#1299) Feature Request: Upgrade swagger2markup version to v1.0.0 duplicate @fayndee 
- (#1227) Bean Validations API (JSR-303) support for Request Parameters @jfiala 
- (#1169) Add Support For Documenting Services In Grails Projects @dilipkrish 
- (#1008) Models with different packages are not represented uniquely in the generated swagger document @tenstriker 
- (#824)  Support vendor extensions in operations @cbornet 
- (#1736) Spring-Data-Rest support for property references

## Maintenance
- (#1701) Make ApiResourceController methods public @psyho 
- (#1694) Fixed the intermittent build failures @dilipkrish 
- (#1675) Why generic method names are being generated for Spring Data Rest?  @tahir
- (#1653) springfox-data-rest: 2.6.1 spring-data-rest: 2.6 RepositoryRestHandlerMapping constructor broken @cbbs 
- (#1644) Update library to support for Spring 5 @binkley 
- (#1628) swagger-ui 2.2.10 @IanSwift 
- (#1621) 2.6.1 breaks @Value placeholder replacement @2is10 
- (#1505) Release process fails when updating the documentation @dilipkrish 

## Bugs
- (#1797) How to write a custom ApiListingScannerPlugin?   @indrabasak 
- (#1786) StackOverflowError In 2.6.1   @tcsw1221 
- (#1785) @ApiOperation "response" value causing docs to ignore model annotations   @bfinleyui 
- (#1780) ConcurrentModificationException on startup with -20170420.041823-43 @gionn 
- (#1778) javax @Valid annotation makes parameter as requestbody-parameter @jmattheis 
- (#1775) Swagger version error in documentation @ersinciftci 
- (#1772) BasePath can't be defined without host @astafev 
- (#1770) SNAPSHOT breaks @Value placeholder replacement @stacysimpson 
- (#1767) Unable to implement and use ApiListingScannerPlugin @stacysimpson 
- (#1749) Error resolving $ref pointer for input DTO @gionn 
- (#1746) How to override API-Documentation of generated endpoints (spring-data-rest)   @florianrusch 
- (#1734) swagger ui not showing the default parameter value zero   @liudonghua123 
- (#1732) Default value of "supportedSubmitMethods" in springfox.js @thadc23 
- (#1727) Jackson required/optional @raderio 
- (#1726) @Size is not working   @raderio 
- (#1725) If you have both Read and Write operation in single Controller readOnly do not work. But if only Write it works.   @dzmitryhil 
- (#1724) Swagger methods in multiple groups being renamed   @nitin02 
- (#1708) @EnableSwagger2 interfering with application configuration.   @rycentious 
- (#1706) X-Forwarded-Port NumberFormatException: For input string: "443,443"  looking-for-contributions @sixcorners 
- (#1698) [BUG]  custom swagger endpoint returns a 404. Default endpoint works.  @ahatzz11 
- (#1697) Problem with direct model substitution   @cbornet 
- (#1677) OAuth2 request adds `vendorExtension` scope to all auth requests @pmlido 
- (#1676) Invalid attributes that starts with x or y   @isolisduran 
- (#1670) Question: how to use @ApiParam annotation on a parameter defined in an interface?   @taxone 
- (#1666) AllowableValues blank for Optional<Enum> parameter @madgnome 
- (#1651) ResponseHeaders do not preserve lexical ordering  question @ahatzz11 
- (#1648) Operation ordering is not working @neil
- (#1632) Invalid response model for class with name "File" @dreambrother 
- (#1623) Swagger annotations like @ApiParam, @ApiOperation annotation work for Spring Data Rest operations @taxone 
- (#1615) api_docs shows content but swagger-ui (2.6.1) is empty @StefanSchubert 
- (#1613) HTML code in API description in ignored using springfox-swagger-ui 2.6.1 @anouarchattouna 
- (#1605) Response with a byte array does not work as expected @maukito 
- (#1603) StackOverflowError on swagger generation @jmattheis 
- (#1597) @ApiParam value is not respected   @sta
- (#1594) IndexOutofBoundException when using unbounded Map models @sac10nikam 
- (#1588) springfox-data-rest : @Param annotation not supported   @tooms4444 
- (#1580) Can't expand the operation when I set @Api tags by chinese  wontfix @letorn 
- (#1571) 2.6.1 Cannot read property of custom enum list @jearton 
- (#1569) When using ApiKey "keyname" is mapped incorrectly  in progress @jmattheis 
- (#1507) Broken basePath with AbstractPathProvider in version 2.5.0 of springfox-swagger2   @danielbcorreia 
- (#1435) Setting a Custom basePath Requires Setting a Static Host in 2.5  looking-for-contributions @asdcdow 

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

