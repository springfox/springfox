package springfox.test.contract.swagger.webflux.bugs;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/bugs")
public class WebFluxBugController {
  @ApiOperation(
      value = "List all projects"
  )
  @ApiResponses(value = {
      @ApiResponse(
          code = 200,
          message = "Content ready"
      )
  })
  @GetMapping(path = "/bug3343", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  Mono<ResponseEntity<Bug3343.SuccessResponse<Bug3343.Payload>>> getAll() {
    return Mono.justOrEmpty(null);
  }
}
