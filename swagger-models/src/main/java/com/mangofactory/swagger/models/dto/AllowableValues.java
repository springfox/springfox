package com.mangofactory.swagger.models.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonSubTypes({
        @JsonSubTypes.Type(value = AllowableListValues.class),
        @JsonSubTypes.Type(value = AllowableRangeValues.class) })
public interface AllowableValues {
}
