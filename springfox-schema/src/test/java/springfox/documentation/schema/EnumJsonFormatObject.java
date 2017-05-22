package springfox.documentation.schema;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by yeh on 22.05.2017.
 */

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EnumJsonFormatObject {
    ONE("One", "This in an enum for number 1"), TWO("Two", "This in an enum for number 2");

    private String name;
    private String description;
    EnumJsonFormatObject(String name, String description){
        this.name=name;
        this.description=description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
