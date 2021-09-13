package springfox.test.contract.oas.linkdedmodels;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.test.contract.oas.linkdedmodels.model.Model;

@RestController
@RequestMapping("/linked-models")
public class LinkedModelsController {

    @GetMapping
    public ResponseEntity<Model> linkedModels() {
        return null;
    }

}
