package de.bytestore.pvheating.handler.devices;

import de.bytestore.pvheating.entity.GPIOType;
import de.bytestore.pvheating.objects.gpio.GPIOItem;

import java.util.ArrayList;

public class Raspberry extends GPIODefinitions {

    @Override
    public String name() {
        return "Raspberry";
    }

    @Override
    public ArrayList<GPIOItem> getPins() {
        ArrayList<GPIOItem> items = new ArrayList<GPIOItem>();

        items.add(new GPIOItem("GPIO2", GPIOType.DIGITAL));
        items.add(new GPIOItem("GPIO3", GPIOType.DIGITAL));
        items.add(new GPIOItem("GPIO4", GPIOType.DIGITAL));
        // GND
        items.add(new GPIOItem("GPIO17", GPIOType.DIGITAL));
        items.add(new GPIOItem("GPIO27", GPIOType.DIGITAL));
        items.add(new GPIOItem("GPIO22", GPIOType.DIGITAL));
        // 3.3V
        items.add(new GPIOItem("GPIO10", GPIOType.DIGITAL));
        items.add(new GPIOItem("GPIO9", GPIOType.DIGITAL));
        items.add(new GPIOItem("GPIO11", GPIOType.DIGITAL));
        // GND
        // RSV
        items.add(new GPIOItem("GPIO5", GPIOType.DIGITAL));
        items.add(new GPIOItem("GPIO6", GPIOType.DIGITAL));
        items.add(new GPIOItem("GPIO13", GPIOType.DIGITAL, GPIOType.PWM));
        items.add(new GPIOItem("GPI19", GPIOType.DIGITAL, GPIOType.PWM));
        items.add(new GPIOItem("GPI26", GPIOType.DIGITAL));
        // GND


        // 5V
        // 5V
        // GND
        // TX
        // RX
        items.add(new GPIOItem("GPIO18", GPIOType.DIGITAL, GPIOType.PWM));
        // GND
        items.add(new GPIOItem("GPIO23", GPIOType.DIGITAL));
        items.add(new GPIOItem("GPIO24", GPIOType.DIGITAL));
        // GND
        items.add(new GPIOItem("GPIO25", GPIOType.DIGITAL));
        items.add(new GPIOItem("GPIO8", GPIOType.DIGITAL));
        items.add(new GPIOItem("GPIO7", GPIOType.DIGITAL));
        // RSV
        // GND
        items.add(new GPIOItem("GPIO12", GPIOType.DIGITAL, GPIOType.PWM));
        // GND
        items.add(new GPIOItem("GPIO16", GPIOType.DIGITAL));
        items.add(new GPIOItem("GPIO20", GPIOType.DIGITAL));
        items.add(new GPIOItem("GPIO21", GPIOType.DIGITAL));
        
        return items;
    }
}
