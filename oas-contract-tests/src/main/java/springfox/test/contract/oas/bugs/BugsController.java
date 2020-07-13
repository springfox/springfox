package springfox.test.contract.oas.bugs;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ExampleProperty;
import io.swagger.annotations.ResponseHeader;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;
import springfox.test.contract.oas.model.Pet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.*;

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

  @ApiResponse(
      code = 200, message = "OK",
      responseHeaders = {
          @ResponseHeader(name = "X-Hello-Bis", description = "X-Hello-Bis header description", response = String.class)
      })
  @ApiOperation(
      responseHeaders = {
          @ResponseHeader(name = "X-Hello", description = "X-Hello header description", response = String.class)
      },
      value = "Get test for response header",
      nickname = "responseHeader", notes = "Notes 'bout test"
  )
  @GetMapping(path = "/bug2684", produces = "text/plain")
  public String bug2684(
      HttpServletRequest req,
      HttpServletResponse resp) {
    resp.addHeader("X-Hello", "Hello!");
    resp.addHeader("X-Hello-Bis", "Hallo!");
    return "Hi!";
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

  @GetMapping("/3380")
  public String bug3380(@ApiIgnore ModelAndView modelAndView) {
    return "success";
  }

  @GetMapping("/3371")
  public Iterable<String> bug3371() {
    return new ArrayList<>();
  }

  @PostMapping(path = "/3311",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> bug3311(
      @RequestPart Bug3311 ticket,
      @RequestPart(required = false) MultipartFile[] attachments) {
    return ResponseEntity.ok(null);
  }

  @PostMapping(path = "/1965", consumes = "multipart/form-data")
  public ResponseEntity<Bug1965> bug1965(
      @Valid @RequestPart(name = "sfParamMap") @RequestParam Map<String, String> paramMap,
      @Valid @RequestPart(name = "sfId") @RequestParam Integer sfId,
      @Valid @RequestPart(name = "sfData") Bug1965 sfData,
      @RequestPart(name = "file", required = false) MultipartFile supportFile) {
    return ResponseEntity.ok(null);
  }

  @RequestMapping(value = "/bug3353", method = POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  @JsonView({Bug3353.Response.class})
  public ResponseEntity<Bug3353.WithdrawQueryRequestView> bug3353(
      @ModelAttribute
      @Validated(Bug3353.IRequest.class)
      @JsonView(Bug3353.Request.class)
          Bug3353.WithdrawQueryRequestView view) {
    return ResponseEntity.ok(null);
  }

  @RequestMapping(value = "/bug1370", method = RequestMethod.POST)
  @ApiOperation(value = "upload attach file", httpMethod = "POST")
  public void bug1370(@ModelAttribute Bug1370 pojo) {
  }

  @PostMapping("/bug3087")
  public void bug3087(@RequestBody Bug3087 test) {
  }

} 
