package com.mangofactory.swagger.dummy;

import com.wordnik.swagger.annotations.Api;
import org.springframework.stereotype.Controller;

@Controller
@Api(value = "Group name", description="Group description")
public class DummyController {

   public void dummyMethod(){

   }
}
