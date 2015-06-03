# 2.0.1 Release Notes
#759 [maintenance] Improve the build workflow 
#754 [maintenance] Provide necessary jars in maven central 
#726 [maintenance] Setter overloading with different Type of param will trigger IllegalArgumentException (thanks  @gaplo917)
#734 [feature] @RequestBody(required = true) does not render required params (thanks @who) 
#664 [feature] Array[enum] parsed as Array[String] in request feature (thanks @hestad)
#747 [bug] Problems using Map<String, String> requests in responses (thanks @akurdyukov)
#740 [bug] List<Map<String, String>> in models not rendered correctly (thanks @nyddogghr)
#733 [bug] ApiResponses cannot be customized/overridden (thanks @who) 
#728 [bug] Request the swagger JSON will throw java.lang.NullPointerException (thanks @gaplo917)
#727 [bug] ApiListingScanner doesn't work correctly for 2 ResourceGroups with the same name (thanks @dplacinta)
#717 [bug] Multiple controllers containing same parts of the URL gets missed/overridden in the spec (thanks @rajeshkamal)
#713 [bug] Swagger UI page with operations open: browser page refresh opens Swagger Petstore operations (thanks  @keesvandieren)
#707 [bug] Controller bean class matters in Resource Group (Java) (thanks @HiPwrD64) 
#702 [bug] @ApiResponses 2.0.0 Snapshot - not rendering - disabled useDefaultResponseMessages (thanks @rajeshkamal)
#688 [bug] Parameter Data Type does not print (thanks @pprabhu3430) 

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