package de.bytestore.pvheating.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum GPIOChannelType implements EnumClass<String> {

    INPUT("INPUT"),
    OUTPUT("OUTPUT");

    private final String id;

    GPIOChannelType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static GPIOChannelType fromId(String id) {
        for (GPIOChannelType at : GPIOChannelType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}