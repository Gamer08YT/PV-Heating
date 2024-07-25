package de.bytestore.pvheating.objects.gpio;

import de.bytestore.pvheating.entity.GPIOType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class GPIOItem {
    @Getter
    private ArrayList<GPIOType> type = new ArrayList<>();

    @Getter
    private String name = "GPIO0";


    public GPIOItem(String name, GPIOType... type) {
        this.type.addAll(List.of(type));
        this.name = name;
    }

    public ArrayList<GPIOType> getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
