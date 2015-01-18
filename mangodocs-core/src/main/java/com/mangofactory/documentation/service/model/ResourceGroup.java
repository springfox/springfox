package com.mangofactory.documentation.service.model;

import com.google.common.base.Objects;

import static com.google.common.base.Objects.*;

public class ResourceGroup {
  private final String groupName;
  private final Class<?> controllerClazz;
  private final Integer position;

  public ResourceGroup(String groupName, Class<?> controllerClazz) {
    this(groupName, controllerClazz, 0);
  }

  public ResourceGroup(String groupName, Class<?> controllerClazz, Integer position) {
    this.groupName = groupName;
    this.controllerClazz = controllerClazz;
    this.position = position;
  }

  public String getGroupName() {
    return groupName;
  }

  public Integer getPosition() {
    return position;
  }

  public Class<?> getControllerClass() {
    return controllerClazz;
  }

  @Override
  public boolean equals(Object obj) {

    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }
    final ResourceGroup rhs = (ResourceGroup) obj;

    return equal(this.groupName, rhs.groupName)
      && equal(this.position, rhs.position)
      && equal(this.controllerClazz, rhs.controllerClazz);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(groupName, controllerClazz, position);
  }

  @Override
  public String toString() {
    return String.format("ResourceGroup{groupName='%s', position=%d, controller=%s}", groupName, position,
            controllerClazz.getName());
  }
}
