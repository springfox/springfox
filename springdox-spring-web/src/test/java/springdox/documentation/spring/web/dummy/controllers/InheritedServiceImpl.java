package springdox.documentation.spring.web.dummy.controllers;

import org.springframework.stereotype.Component;


@Component
public class InheritedServiceImpl implements InheritedService {

  @Override
  public String getSomething(String parameter) {
    return parameter;
  }


}