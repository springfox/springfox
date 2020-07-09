package springfox.test.contract.oas.features;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/features")
public class FeatureDemonstrationService {
  @GetMapping("/2613")
  public Feature2613 getfeature() {
    return new Feature2613();
  }
}
