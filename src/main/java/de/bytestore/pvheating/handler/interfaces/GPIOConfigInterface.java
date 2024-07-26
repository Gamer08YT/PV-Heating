package de.bytestore.pvheating.handler.interfaces;

import de.bytestore.pvheating.objects.gpio.GPIOItem;

import java.util.ArrayList;

public interface GPIOConfigInterface {
    String name();

    ArrayList<GPIOItem> getPins();
}
