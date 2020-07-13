package springfox.test.contract.swagger.webflux;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/features")
public class FeatureDemonstrationController {
  @GetMapping("/response-mono")
  public ResponseEntity<Mono<String>> helloMono() {
    return ResponseEntity.ok(Mono.fromSupplier(() -> "Hello SpringFox!"));
  }

  @GetMapping("/mono")
  public Mono<String> helloPerson(String name) {
    return Mono.just("Hello " + name + "!");
  }

  @GetMapping("/flux")
  public Flux<String> helloPeople(String... names) {
    return Flux.fromArray(names).map(name -> "Hello " + name);
  }

  @GetMapping("/response-flux")
  public ResponseEntity<Flux<String>> helloPeople(List<String> names) {
    return ResponseEntity.of(Optional.of(Flux.fromStream(names.stream().map(name -> "Hello " + name))));
  }

  @PostMapping(value = "/3364/{name}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public boolean feature3364(@RequestPart("files") Flux<FilePart> filePartFlux, @PathVariable final String name) {
    return true;
  }
}
