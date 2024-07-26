package de.bytestore.pvheating.objects.gpio;

import de.bytestore.pvheating.entity.GPIOType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GPIOItem {
    private ArrayList<GPIOType> type = new ArrayList<>();

    private String name = "GPIO0";


    private String definition = "";


    public GPIOItem(String name, GPIOType... type) {
        this.type.addAll(List.of(type));
        this.name = name;
    }
}
