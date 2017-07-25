package springfox.documentation.spring.web.dummy.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Api(description = "Services to demonstrate produces/consumes override behaviour on document and operation level")
@RequestMapping(path = "/consumes-produces")
public class ConsumesProducesService {

    @GetMapping("/without-operation-produces")
    @ApiOperation("Does not have operation produces defined")
    public String withoutOperationProduces() {
        throw new UnsupportedOperationException();
    }

    @GetMapping(value = "/with-operation-produces", produces = MediaType.APPLICATION_XML_VALUE)
    @ApiOperation("Does have operation produces defined")
    public String withOperationProduces() {
        throw new UnsupportedOperationException();
    }

    @PostMapping("/without-operation-consumes")
    @ApiOperation("Does not have operation consumes defined")
    public void withoutOperationConsumes(@RequestBody String test) {
        throw new UnsupportedOperationException();
    }

    @PostMapping(value = "/with-operation-consumes", consumes = MediaType.APPLICATION_XML_VALUE)
    @ApiOperation("Does have operation consumes defined")
    public void withOperationConsumes(@RequestBody String test) {
        throw new UnsupportedOperationException();
    }

    @PostMapping(value = "/with-operation-consumes-produces", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    @ApiOperation("Does have operation consumes and produces defined")
    public void withOperationConsumesAndProduces(@RequestBody String test) {
        throw new UnsupportedOperationException();
    }
}
