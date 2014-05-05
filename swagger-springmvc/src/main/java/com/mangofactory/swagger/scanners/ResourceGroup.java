package com.mangofactory.swagger.scanners;

import com.google.common.base.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;

public class ResourceGroup {
   private String groupName;

   public ResourceGroup(String groupName) {
      this.groupName = groupName;
   }

   public String getGroupName() {
      return groupName;
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
      ResourceGroup rhs = (ResourceGroup) obj;
      return new EqualsBuilder()
              .append(this.groupName, rhs.groupName)
              .isEquals();
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(groupName);
   }

   @Override
   public String toString() {
      return "ResourceGroup{" +
              "groupName='" + groupName + '\'' +
              '}';
   }
}
