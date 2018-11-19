# Springfox Spring-Integration WebFlux

Adds basic automatic recognition of SpringFox documentation for spring-integration WebFlux inbound http endpoints. 

This does not include documentation of example responses. For information about providing example responses see the 
readme file of the `springfox-spring-integration` module.

To enable springfox in your spring-integration application, add the following dependencies:

```
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger2</artifactId>
        <version>${springfox.version}</version>
    </dependency>
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-spring-webflux</artifactId>
        <version>${springfox.version}</version>
    </dependency>
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-spring-integration-webmvc</artifactId>
        <version>${springfox.version}</version>
    </dependency>
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger-ui</artifactId>
        <version>${springfox.version}</version>
    </dependency>
```