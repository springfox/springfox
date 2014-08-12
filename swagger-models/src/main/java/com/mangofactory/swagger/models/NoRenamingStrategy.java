package com.mangofactory.swagger.models;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("namingStrategy")
public class NoRenamingStrategy implements NamingStrategy{

  @Override
  public String name(String currentName) {
    return currentName;
  }
}
