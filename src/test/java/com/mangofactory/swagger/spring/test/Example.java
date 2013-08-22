package com.mangofactory.swagger.spring.test;

public class Example {
    private String foo;
    private int bar;
    private EnumType enumType;
    private NestedType nestedType;

    public Example(String foo, int bar, EnumType enumType, NestedType nestedType) {
        this.foo = foo;
        this.bar = bar;
        this.enumType = enumType;
        this.nestedType = nestedType;
    }

    public String getFoo() {
        return foo;
    }

    public int getBar() {
        return bar;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public void setBar(int bar) {
        this.bar = bar;
    }

    public EnumType getEnumType() {
        return enumType;
    }

    public void setEnumType(EnumType enumType) {
        this.enumType = enumType;
    }

    public NestedType getNestedType() {
        return nestedType;
    }

    public void setNestedType(NestedType nestedType) {
        this.nestedType = nestedType;
    }
}

