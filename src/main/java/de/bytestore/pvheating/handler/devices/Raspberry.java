package de.bytestore.pvheating.handler.devices;

import de.bytestore.pvheating.entity.GPIOType;
import de.bytestore.pvheating.objects.gpio.GPIOItem;

public class Raspberry extends GPIOConfig {
    public Raspberry() {
        this.getItems().add(new GPIOItem("GPIO2", GPIOType.DIGITAL));
        this.getItems().add(new GPIOItem("GPIO3", GPIOType.DIGITAL));
        this.getItems().add(new GPIOItem("GPIO4", GPIOType.DIGITAL));
        // GND
        this.getItems().add(new GPIOItem("GPIO17", GPIOType.DIGITAL));
        this.getItems().add(new GPIOItem("GPIO27", GPIOType.DIGITAL));
        this.getItems().add(new GPIOItem("GPIO22", GPIOType.DIGITAL));
        // 3.3V
        this.getItems().add(new GPIOItem("GPIO10", GPIOType.DIGITAL));
        this.getItems().add(new GPIOItem("GPIO9", GPIOType.DIGITAL));
        this.getItems().add(new GPIOItem("GPIO11", GPIOType.DIGITAL));
        // GND
        // RSV
        this.getItems().add(new GPIOItem("GPIO5", GPIOType.DIGITAL));
        this.getItems().add(new GPIOItem("GPIO6", GPIOType.DIGITAL));
        this.getItems().add(new GPIOItem("GPIO13", GPIOType.DIGITAL, GPIOType.PWM));
        this.getItems().add(new GPIOItem("GPI19", GPIOType.DIGITAL, GPIOType.PWM));
        this.getItems().add(new GPIOItem("GPI26", GPIOType.DIGITAL));
        // GND


        // 5V
        // 5V
        // GND
        // TX
        // RX
        this.getItems().add(new GPIOItem("GPIO18", GPIOType.DIGITAL, GPIOType.PWM));
        // GND
        this.getItems().add(new GPIOItem("GPIO23", GPIOType.DIGITAL));
        this.getItems().add(new GPIOItem("GPIO24", GPIOType.DIGITAL));
        // GND
        this.getItems().add(new GPIOItem("GPIO25", GPIOType.DIGITAL));
        this.getItems().add(new GPIOItem("GPIO8", GPIOType.DIGITAL));
        this.getItems().add(new GPIOItem("GPIO7", GPIOType.DIGITAL));
        // RSV
        // GND
        this.getItems().add(new GPIOItem("GPIO12", GPIOType.DIGITAL, GPIOType.PWM));
        // GND
        this.getItems().add(new GPIOItem("GPIO16", GPIOType.DIGITAL));
        this.getItems().add(new GPIOItem("GPIO20", GPIOType.DIGITAL));
        this.getItems().add(new GPIOItem("GPIO21", GPIOType.DIGITAL));
    }
}
