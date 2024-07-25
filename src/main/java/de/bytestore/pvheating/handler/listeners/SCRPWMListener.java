package de.bytestore.pvheating.handler.listeners;

import de.bytestore.pvheating.entity.GPIOType;
import de.bytestore.pvheating.handler.interfaces.GPIOListener;
import de.bytestore.pvheating.objects.gpio.GPIOItem;

public class SCRPWMListener implements GPIOListener {
    @Override
    public String name() {
        return "scr-pwm";
    }

    @Override
    public void register(GPIOItem gpioIO) {

    }

    @Override
    public GPIOType type() {
        return GPIOType.PWM;
    }
}
