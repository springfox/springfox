package springfox.documentation.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class TypeWithFieldsAndGettersAnnotatedWithJsonPropertyAndApiPropertyModel {

    @JsonProperty(required = false)
    @ApiModelProperty(required = true)
    private int apiModelPropertyAnnotatedField;

    @JsonProperty(required = true)
    @ApiModelProperty(required = false)
    private int jsonPropertyAnnotatedField;

    @JsonProperty(required = false)
    @ApiModelProperty(required = false)
    private int jsonPropertyAndApiModelPropertyAnnotatedField;

    private int apiModelPropertyAnnotatedGetter;

    private int jsonPropertyAnnotatedGetter;

    private int jsonPropertyAndApiModelPropertyAnnotatedGetter;


    public int getApiModelPropertyAnnotatedField() {
        return apiModelPropertyAnnotatedField;
    }

    public int getJsonPropertyAnnotatedField() {
        return jsonPropertyAnnotatedField;
    }

    public int getJsonPropertyAndApiModelPropertyAnnotatedField() {
        return jsonPropertyAndApiModelPropertyAnnotatedField;
    }

    @JsonProperty(required = false)
    @ApiModelProperty(required = true)
    public int getApiModelPropertyAnnotatedGetter() {
        return apiModelPropertyAnnotatedGetter;
    }

    @JsonProperty(required = true)
    @ApiModelProperty(required = false)
    public int getJsonPropertyAnnotatedGetter() {
        return jsonPropertyAnnotatedGetter;
    }

    @JsonProperty(required = false)
    @ApiModelProperty(required = false)
    public int getJsonPropertyAndApiModelPropertyAnnotatedGetter() {
        return jsonPropertyAndApiModelPropertyAnnotatedGetter;
    }
}
