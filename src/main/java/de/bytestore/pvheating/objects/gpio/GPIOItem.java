package de.bytestore.pvheating.objects.gpio;

import de.bytestore.pvheating.entity.GPIOType;
import lombok.Getter;

public class GPIOItem {
    @Getter
    private GPIOType type = GPIOType.DIGITAL;

    @Getter
    private int name = 0;

    public GPIOItem(GPIOType type, int name) {
        this.type = type;
        this.name = name;
    }

    public GPIOType getType() {
        return type;
    }

    public int getName() {
        return name;
    }
}
