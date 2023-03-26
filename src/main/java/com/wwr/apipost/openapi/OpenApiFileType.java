package com.wwr.apipost.openapi;

public enum OpenApiFileType {
    JSON(".json"),
    YAML(".yaml");

    private String suffix;

    OpenApiFileType(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }
}
