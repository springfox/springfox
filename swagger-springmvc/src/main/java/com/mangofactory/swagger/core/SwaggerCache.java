package com.mangofactory.swagger.core;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.mangofactory.service.model.Group;

import java.util.Map;

import static com.google.common.collect.Maps.*;

public class SwaggerCache {
  private Map<String, Group> groupLookup = newLinkedHashMap();

  public void addGroup(Group group) {
    groupLookup.put(group.getGroupName(), group);
  }

  public Group getGroup(String groupName) {
    if (Strings.isNullOrEmpty(groupName)) {
      Iterables.getFirst(groupLookup.values(), null);
    }
    return groupLookup.get(groupName);
  }

}
