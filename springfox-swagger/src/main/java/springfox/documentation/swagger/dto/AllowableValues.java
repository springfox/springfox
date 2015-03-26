package springfox.documentation.swagger.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonSubTypes({
        @JsonSubTypes.Type(value = AllowableListValues.class),
        @JsonSubTypes.Type(value = AllowableRangeValues.class) })
public interface AllowableValues {
}
