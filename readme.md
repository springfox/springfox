# Swagger / Spring MVC Integration

This project provides integration between [Swagger](https://github.com/wordnik/swagger-core) and Spring MVC.

Spring beans annotated with `@Controller` are detected and parsed for documentation.

## Supported annotations
Currently, a subset of Swagger annotations are supported.  Support will improve over the coming releases.

All `@Controller` classes are parsed, and methods annotated with `@RequestMapping` are generated.

Additionally, `@Api` at the class level, and `@ApiOperation` at the method level are both supported.

### Not supported
 * `@ApiErrors` and `@ApiError` [Issue](https://github.com/martypitt/swagger-springmvc/issues/1)
 * JSON models are not documented [Issue](https://github.com/martypitt/swagger-springmvc/issues/2)

## Getting started
To wire up support, add the following into your ``*-servlet.xml` context:

    <bean id="documentationController" class="com.mangofactory.swagger.springmvc.controller.DocumentationController"
	    p:apiVersion="1.0"
    	p:swaggerVersion="1.0"
        p:basePath="http://www.mydomain.com/swagger-springmvc-example/" />

The `basePath` property is external-facing url the maps to your SpringMVC dispatcher servlet.

This creates a controller at `/apidoc` from this uri, which serves swagger's raw documentation in JSON format.  (eg., In the above example,  `http://www.mydomain.com/swagger-springmvc-example/apidoc`)

## Example project
An example of Swaggers PetStore in Spring MVC is available [here](https://github.com/martypitt/swagger-springmvc-example)