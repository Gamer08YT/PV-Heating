package de.bytestore.pvheating.handler;

import de.bytestore.pvheating.handler.devices.GPIODefinitions;
import de.bytestore.pvheating.handler.devices.Raspberry;
import de.bytestore.pvheating.handler.interfaces.ProviderTemplateInterface;
import de.bytestore.pvheating.handler.templates.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class GPIOHandler {
    private static final Logger log = LoggerFactory.getLogger(GPIOHandler.class);

    /**
     * Register the default GPIO configurations.
     *
     * @return
     */
    public static ArrayList<GPIODefinitions> getConfigs() {
        ArrayList<GPIODefinitions> configs = new ArrayList<GPIODefinitions>();

        configs.add(new Raspberry());

        return configs;
    }

    /**
     * Returns a list of GPIOListeners.
     *
     * @return the list of GPIOListeners
     */
    public static ArrayList<ProviderTemplateInterface> getListeners() {
        ArrayList<ProviderTemplateInterface> listeners = new ArrayList<ProviderTemplateInterface>();

        listeners.add(new PumpTemplate());
        listeners.add(new SCRAnalogListener());
        listeners.add(new SCRPWMListener());
        listeners.add(new TemperatureAnalogListener());
        listeners.add(new TemperatureDigitalListener());

        return listeners;
    }

    public void enablePin() {

    }

}
