package com.multi_tenant.demo.Enums;

public enum Term
{
    DAY("1"),
    WEEK("7"),
    MONTH("31"),
    QUATER("122"),
    YEAR("365");

    private final String value;

    Term(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
