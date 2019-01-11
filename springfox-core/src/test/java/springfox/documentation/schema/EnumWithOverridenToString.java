package springfox.documentation.schema;

public enum EnumWithOverridenToString {
    ONE("one-string"),
    TWO("two-string");

    private final String customName;

    EnumWithOverridenToString(String customName) {
        this.customName = customName;
    }

    @Override
    public String toString() {
        return customName;
    }
}
