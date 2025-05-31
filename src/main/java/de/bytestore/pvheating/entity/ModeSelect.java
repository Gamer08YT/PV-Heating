package de.bytestore.pvheating.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum ModeSelect implements EnumClass<String> {

    STANDBY("standby"),
    DYNAMIC("dynamic"),
    CONSUME("consume");

    private final String id;

    ModeSelect(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ModeSelect fromId(String id) {
        for (ModeSelect at : ModeSelect.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}