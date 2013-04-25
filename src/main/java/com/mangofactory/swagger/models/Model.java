package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;

public class Model {
    private String name;
    private ResolvedType type;
    private final boolean returnType;

    public Model(String name, ResolvedType type) {
        this.name = name;
        this.type = type;
        this.returnType = false;
    }

    public Model(String name, ResolvedType type, boolean returnType) {
        this.name = name;
        this.type = type;
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public ResolvedType getType() {
        return type;
    }

    public boolean isReturnType() {
        return returnType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Model model = (Model) o;

        if (!name.equals(model.name)) {
            return false;
        }
        if (!type.equals(model.type)) {
            return false;
        }
        if (!returnType == model.returnType) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + Boolean.valueOf(returnType).hashCode();
        return result;
    }
}
