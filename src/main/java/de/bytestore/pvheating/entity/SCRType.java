package de.bytestore.pvheating.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum SCRType implements EnumClass<Integer> {

    PWM(10),
    VOLTAGE(20),
    CURRENT(30);

    private final Integer id;

    SCRType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static SCRType fromId(Integer id) {
        for (SCRType at : SCRType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}