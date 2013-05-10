# Swagger / Spring MVC Integration

This project provides integration between [Swagger](https://github.com/wordnik/swagger-core) and Spring MVC.

Spring beans annotated with `@Controller` are detected and parsed for documentation.

## Getting started
The project is available from maven:

	<dependency>
  		<groupId>com.mangofactory</groupId>
  		<artifactId>swagger-springmvc</artifactId>
  		<version>0.5.0</version>
	</dependency>
	

## Supported annotations
Currently, a subset of Swagger annotations are supported.  Support will improve over the coming releases.

All `@Controller` classes are parsed, and methods annotated with `@RequestMapping` are generated.
  
Additionally, `@Api` at the class level, and `@ApiOperation` at the method level are both supported.
			
## Getting started
To wire up support, add the following into your ``*-servlet.xml` context:


    <context:component-scan base-package="com.mangofactory.swagger.spring.controller" use-default-filters="false">
        <context:include-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
    </context:component-scan>
    <bean id="swaggerConfiguration" class="com.mangofactory.swagger.SwaggerConfiguration">
        <property name="basePath" value="http://www.mydomain.com/swagger-springmvc-example/"/>
        <property name="apiVersion" value="1.0"/>
        <property name="excludedResources">
            <list>
                <value>/controller-uri-to-exclude</value>
            </list>
        </property>
    </bean>

The `basePath` property is external-facing url the maps to your SpringMVC dispatcher servlet.

This creates a controller at `/api-docs` from this uri, which serves swagger's raw documentation in JSON format.  (eg
., In the above example,  `http://www.mydomain.com/swagger-springmvc-example/api-docs`)

## Deviations from default Swagger API
Some deviations from the default Swagger API exist.  Wherever possible, these are inteded to be implemented as-well-as the default Swagger implementation, rather than as a replacement.

The overarching goal is to support generation of the Swagger JSON, with minimal intrusion to the code itself.

### Errors
Declaration of errors supports the standard Swagger `@ApiErrors` and `@ApiError` annotations.
In addition, there are `com.mangofactory.swagger` implementations of these that reduce the amount of per-method code (notably, at the cost of some flexibility)

`@ApiError` is now supported at the exception class level, as shown here:

    @ApiError(code=302,reason="Malformed request")
    public class BadRequestException {}

This allows errors to be declared as follows:

	@ApiErrors({NotFoundException.class,BadRequestException.class})
	public void someApiMethod() {};

or, simply using a `throws` declaration:

	public void someApiMethod() throws NotFoundException, BadRequestException {};

## Example project
An example of Swaggers PetStore in Spring MVC is available [here](https://github.com/martypitt/swagger-springmvc-example)

## TODO:
- Handle the case where RequestMapping might have wildcards

License
-------

Copyright 2012 Marty Pitt - [martypitt](https://github.com/martypitt), Dilip Krishnan - [@dilipkrish](https://github.com/dilipkrish)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at [apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

