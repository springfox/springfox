package springdox.documentation.spring.web.dummy.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import springdox.documentation.spring.web.dummy.models.Pet;

@Controller
@RequestMapping("/foo")
public class ConcreteController extends AbstractController<Pet> {
}
