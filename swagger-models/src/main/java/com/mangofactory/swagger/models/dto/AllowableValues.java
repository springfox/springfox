package com.mangofactory.swagger.models.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.mangofactory.swagger.dto.AllowableListValues;
import com.mangofactory.swagger.dto.AllowableRangeValues;

@JsonSubTypes({
        @JsonSubTypes.Type(value = AllowableListValues.class),
        @JsonSubTypes.Type(value = AllowableRangeValues.class) })
public interface AllowableValues {
}
