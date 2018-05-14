package com.trivia.core.utility;



public enum SortOrder {
    ASCENDING("ASCENDING"),
    DESCENDING("DESCENDING"),
    DEFAULT("DEFAULT");

    private String sortName;

    SortOrder(String sortName) {
        this.sortName = sortName;
    }

    public String sortName() {
        return sortName;
    }
}