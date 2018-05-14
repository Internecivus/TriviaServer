package com.trivia.persistence.entity;


import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum RoleType {
    CONTRIBUTOR(Name.CONTRIBUTOR),
    PROVIDER(Name.PROVIDER),
    MODERATOR(Name.MODERATOR),
    ADMIN(Name.ADMIN);

    private String name;

    RoleType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static class Name {
        public static final String CONTRIBUTOR = "CONTRIBUTOR";
        public static final String PROVIDER = "PROVIDER";
        public static final String MODERATOR = "MODERATOR";
        public static final String ADMIN = "ADMIN";

        // Only used for business logic.
        public static final String PRINCIPAL = "PRINCIPAL";
        public static final String USER = "USER";
        public static final String CLIENT = "CLIENT";
    }

    public Set<String> getAll() {
        return Arrays.stream(values()).map(n -> getName()).collect(Collectors.toSet());
    } //TODO: Needed?
}