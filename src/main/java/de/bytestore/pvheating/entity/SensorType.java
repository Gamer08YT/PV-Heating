package de.bytestore.pvheating.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum SensorType implements EnumClass<Integer> {

    NTC(10),
    PTC(20);

    private final Integer id;

    SensorType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static SensorType fromId(Integer id) {
        for (SensorType at : SensorType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}