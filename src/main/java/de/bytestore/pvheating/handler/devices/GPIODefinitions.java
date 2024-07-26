package de.bytestore.pvheating.handler.devices;

import de.bytestore.pvheating.handler.interfaces.GPIOConfigInterface;
import de.bytestore.pvheating.objects.gpio.GPIOItem;

import java.util.ArrayList;

public class GPIODefinitions implements GPIOConfigInterface {
    @Override
    public String name() {
        return "";
    }

    @Override
    public ArrayList<GPIOItem> getPins() {
        return null;
    }

    /**
     * Retrieves a GPIOItem object based on the given name.
     *
     * @param nameIO The name of the GPIO pin.
     * @return The GPIOItem object matching the given name, or null if no match is found.
     */
    public GPIOItem getPinByName(String nameIO) {
        for (GPIOItem item : getPins()) {
            if (item.getName().equals(nameIO))
                return item;
        }

        return null;
    }
}
