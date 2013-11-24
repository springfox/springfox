package com.mangofactory.swagger.readers;

public interface Command<T> {
   public void execute(T context);
}
