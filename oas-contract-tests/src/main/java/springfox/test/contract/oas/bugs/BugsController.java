package springfox.test.contract.oas.bugs;

import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
} 
