package com.mangofactory.documentation.spring.web;

import com.mangofactory.documentation.service.Documentation;

import java.util.Map;

import static com.google.common.collect.Maps.*;

public class GroupCache {
  private Map<String, Documentation> groupLookup = newLinkedHashMap();

  public void addGroup(Documentation documentation) {
    groupLookup.put(documentation.getGroupName(), documentation);
  }

  public Documentation getGroup(String groupName) {
    return groupLookup.get(groupName);
  }

}
