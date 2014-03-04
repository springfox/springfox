package com.mangofactory.swagger.scanners;

import com.google.common.base.Objects;

public class ResourceGroup {
    private String groupName;
    private String realUri;

    public ResourceGroup(String groupName, String realUri) {
        this.groupName = groupName;
        this.realUri = realUri;
    }

    public ResourceGroup(String groupName) {
        this(groupName, String.format("/%s", groupName));
    }

    public String getGroupName() {
        return groupName;
    }

    public String getRealUri() {
        return realUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResourceGroup that = (ResourceGroup) o;

        return Objects.equal(groupName, that.groupName) && Objects.equal(realUri, that.realUri);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(groupName, realUri);
    }

    @Override
    public String toString() {
        return "ResourceGroup{" +
                "groupName='" + groupName + '\'' +
                ", realUri='" + realUri + '\'' +
                '}';
    }
}
