package de.bytestore.pvheating.objects.config;

import de.bytestore.pvheating.handler.devices.GPIODefinitions;
import lombok.Data;

import java.util.ArrayList;

@Data
public class GPIOConfig {
    private ArrayList<GPIODefinitions> definitions = new ArrayList<GPIODefinitions>();

    /**
     * Retrieves a GPIODefinitions object by its name.
     *
     * @param nameIO the name of the GPIODefinitions object to retrieve
     * @return the GPIODefinitions object with the specified name, or null if not found
     */
    public GPIODefinitions getProviderByName(String nameIO) {
        for (GPIODefinitions gpioDefinitions : definitions) {
            if (gpioDefinitions.name().equals(nameIO))
                return gpioDefinitions;
        }

        return null;
    }
}
