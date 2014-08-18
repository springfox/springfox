package com.mangofactory.swagger.dummy.controllers;

import com.mangofactory.swagger.dummy.models.Pet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/foo")
public class ConcreteController extends AbstractController<Pet> {
}
