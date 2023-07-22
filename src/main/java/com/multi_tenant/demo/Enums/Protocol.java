package com.multi_tenant.demo.Enums;

public enum Protocol
{
    HTTP("http"),
    HTTPS("https");

    private final String value;

    Protocol(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
