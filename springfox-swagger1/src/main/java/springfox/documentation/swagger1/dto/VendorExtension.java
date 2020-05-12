package springfox.documentation.swagger1.dto;

public class VendorExtension {
    private String name;
    private Object value;

    public VendorExtension() {
        // zero arg constructor
    }

    public VendorExtension(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
