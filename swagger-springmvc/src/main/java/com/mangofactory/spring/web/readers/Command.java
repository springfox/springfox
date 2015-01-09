package com.mangofactory.spring.web.readers;

public interface Command<T>  {
  public void execute(T context);
}
