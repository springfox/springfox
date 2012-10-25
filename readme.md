# Swagger / Spring MVC Integration

This project provides integration between [Swagger](https://github.com/wordnik/swagger-core) and Spring MVC.

Spring beans annotated with `@Controller` are detected and parsed for documentation.

## Getting started
The project is available from maven:

	<dependency>
  		<groupId>com.mangofactory</groupId>
  		<artifactId>swagger-springmvc</artifactId>
  		<version>0.2.4</version>
	</dependency>
	

## Supported annotations
Currently, a subset of Swagger annotations are supported.  Support will improve over the coming releases.

All `@Controller` classes are parsed, and methods annotated with `@RequestMapping` are generated.
  
Additionally, `@Api` at the class level, and `@ApiOperation` at the method level are both supported.

### Not supported
 * JSON models are not documented [Issue](https://github.com/martypitt/swagger-springmvc/issues/2)

## Getting started
To wire up support, add the following into your ``*-servlet.xml` context:

    <bean id="documentationController" class="com.mangofactory.swagger.springmvc.controller.DocumentationController"
	    p:apiVersion="1.0"
    	p:swaggerVersion="1.0"
        p:basePath="http://www.mydomain.com/swagger-springmvc-example/" />

The `basePath` property is external-facing url the maps to your SpringMVC dispatcher servlet.

This creates a controller at `/apidoc` from this uri, which serves swagger's raw documentation in JSON format.  (eg., In the above example,  `http://www.mydomain.com/swagger-springmvc-example/apidoc`)

## Deviations from default Swagger API
Some deviations from the default Swagger API exist.  Wherever possible, these are inteded to be implemented as-well-as the default Swagger implementation, rather than as a replacement.

The overarching goal is to support generation of the Swagger JSON, with minimal intrusion to the code itself.

### Errors
Declaration of errors supports the standard Swagger `@ApiErrors` and `@ApiError` annotations.
In addition, there are `com.mangofactory.swagger` implementations of these that reduce the amount of per-method code (notably, at the cost of some flexibility)

`@ApiError` is now supported at the exception class level, as shown here:

    @ApiError(code=302,reason="Malformed request")
    public class BadRequestException {}

This allows errors to be delcared as follows:

	@ApiErrors({NotFoundException.class,BadRequestException.class})
	public void someApiMethod() {};

or, simply using a `throws` declaration:

	public void someApiMethod() throws NotFoundException, BadRequestException {};

## Example project
An example of Swaggers PetStore in Spring MVC is available [here](https://github.com/martypitt/swagger-springmvc-example)