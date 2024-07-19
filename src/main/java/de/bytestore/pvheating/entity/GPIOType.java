package de.bytestore.pvheating.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum GPIOType implements EnumClass<Integer> {

    ANALOG(10),
    DIGITAL(20);

    private final Integer id;

    GPIOType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static GPIOType fromId(Integer id) {
        for (GPIOType at : GPIOType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}