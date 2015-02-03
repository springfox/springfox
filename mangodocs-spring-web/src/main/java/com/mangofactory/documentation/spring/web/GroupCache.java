package com.mangofactory.documentation.spring.web;

import com.mangofactory.documentation.service.Group;

import java.util.Map;

import static com.google.common.collect.Maps.*;

public class GroupCache {
  private Map<String, Group> groupLookup = newLinkedHashMap();

  public void addGroup(Group group) {
    groupLookup.put(group.getGroupName(), group);
  }

  public Group getGroup(String groupName) {
    return groupLookup.get(groupName);
  }

}
