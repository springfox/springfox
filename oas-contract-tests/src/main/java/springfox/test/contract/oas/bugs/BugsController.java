package springfox.test.contract.oas.bugs;

import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ExampleProperty;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import springfox.test.contract.oas.model.Pet;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/bugs")
public class BugsController {
  @GetMapping("/3338")
  public Object bug3338(@NotNull String username) {
    return null;
  }

  @GetMapping("/3329")
  public Object bug3329(@Size(max = 36, min = 36) UUID uuid) {
    return null;
  }

  @GetMapping("/3321")
  public void bug3321(
      @ApiParam(allowableValues = "one, two, three")
      @RequestParam(name = "expand", required = false) Set<NumberEnum> numbers) {
  }

  @GetMapping(value = "/3348/{callId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public Mono<ResponseEntity<InputStreamResource>> bug3348(@PathVariable("callId") String callId) {
    return null;
  }

  @PostMapping(path = "/3351/{id}")
  public ResponseEntity<String> createPost(
      @PathVariable final int id,
      @RequestBody final Pet post) {
    return ResponseEntity.ok("Success");
  }

  @PostMapping("/2982/model")
  public void bug2982Model(Bug2982.MyClass input) {
  }

  @PostMapping("/2982/body")
  public void bug2982Body(@RequestBody Bug2982.MyClass input) {
  }


  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Not Found",
          examples = @io.swagger.annotations.Example(
              value = {
                  @ExampleProperty(
                      mediaType = "application/json",
                      value = "{'invalidField': 'address'}"),
                  @ExampleProperty(
                      mediaType = "text/plain",
                      value = "The address was invalid")}),
          response = Bug2767.ErrorResponse.class)})
  @GetMapping("/2767/swagger15")
  public Bug2767.Response bug2767() {
    return new Bug2767.Response();
  }

  @io.swagger.v3.oas.annotations.responses.ApiResponses(
      value = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "400",
              description = "Bad Input",
              content = {
                  @Content(mediaType = "application/json",
                      examples = {
                          @ExampleObject(name = "foo",
                              summary = "foo example",
                              value = "{'invalidField': 'foo'}",
                              description = "Foo response"
                          ),
                          @ExampleObject(name = "bar",
                              summary = "bar example",
                              value = "{'invalidField': 'bar'}",
                              description = "Bar response"
                          )
                      },
                      schema = @Schema(implementation = Bug2767.ErrorResponse.class))
              }
          )
      }
  )
  @GetMapping("/2767/swagger20")
  public Bug2767.Response bug2767New() {
    return new Bug2767.Response();
  }
} 
