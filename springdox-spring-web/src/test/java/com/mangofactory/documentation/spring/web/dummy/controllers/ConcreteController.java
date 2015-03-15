package com.mangofactory.documentation.spring.web.dummy.controllers;

import com.mangofactory.documentation.spring.web.dummy.models.Pet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/foo")
public class ConcreteController extends AbstractController<Pet> {
}
