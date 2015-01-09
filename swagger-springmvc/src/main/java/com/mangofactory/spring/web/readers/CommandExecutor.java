package com.mangofactory.spring.web.readers;

import java.util.List;

//T - result type, typically Map<String, Object>
public class CommandExecutor<T, C> {

  public T execute(List<? extends Command<C>> commands, CommandContext<T> context) {
    if (null != commands) {
      for (Command command : commands) {
        command.execute(context);
      }
      return context.getResult();
    }
    return null;
  }
}
