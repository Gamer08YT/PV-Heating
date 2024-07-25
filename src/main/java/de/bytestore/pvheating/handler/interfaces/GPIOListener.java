package de.bytestore.pvheating.handler.interfaces;

import de.bytestore.pvheating.entity.GPIOType;
import de.bytestore.pvheating.objects.gpio.GPIOItem;

public interface GPIOListener {
    String name();

    void register(GPIOItem gpioIO);

    GPIOType type();
}
