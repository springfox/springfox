package com.mangofactory.swagger.dummy.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public abstract class AbstractController<T> {

  @RequestMapping(value = "/create-t", method = RequestMethod.PUT)
  public void create(T toCreate) {
    throw new UnsupportedOperationException();
  }

  @RequestMapping(value = "/get-t", method = RequestMethod.GET)
  public T get() {
    throw new UnsupportedOperationException();
  }
}
