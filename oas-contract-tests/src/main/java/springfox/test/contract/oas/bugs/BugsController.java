package springfox.test.contract.oas.bugs;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/bugs")
public class BugsController {
  @GetMapping("/3338")
  public Object bug3338(@NotNull String username) {
    return null;
  }
}
