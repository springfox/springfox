package springfox.test.contract.oas.features;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/features")
public class FeatureDemonstrationService {
  @GetMapping("/2613")
  public Feature2613 getfeature() {
    return new Feature2613();
  }

  @GetMapping("/2831")
  @ApiOperation(
      value = "Demo",
      notes = "Demo optional header error"
  )
  @ApiResponses({
      @ApiResponse(code = 200, message = "okey"),
      @ApiResponse(code = 403, message = "ko"),
      @ApiResponse(code = 404, message = "ko")
  })
  public ResponseEntity<Object> feature2831(
      @ApiParam(value = "foo msg", example = "foo example", type = "header", required = true)
      @RequestHeader("foo") Optional<String> foo) {
    return ResponseEntity.ok(new Object());
  }
}
