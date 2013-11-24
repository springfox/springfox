package com.mangofactory.swagger.core;

import com.mangofactory.swagger.readers.Command;

import java.util.List;

//T - resulttype, typically Map<String, Object>
public class CommandExecutor<T> {
   public T execute(List<Command> commands, CommandContext<T> context){
      if(null != commands){
         for(Command command : commands){
            command.execute(context);
         }
         return context.getResult();
      }
      return null;
   }
}
