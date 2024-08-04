package de.bytestore.pvheating.handler.templates;

import de.bytestore.pvheating.entity.GPIOChannelType;
import de.bytestore.pvheating.entity.GPIOType;

public class TemperatureInTemplate extends ProviderTemplate {
    @Override
    public String name() {
        return "temperature-in";
    }

    @Override
    public GPIOType[] type() {
        return new GPIOType[] {GPIOType.ANALOG, GPIOType.MODBUS, GPIOType.WIRE1};
    }

    @Override
    public GPIOChannelType channelType() {
        return GPIOChannelType.INPUT;
    }
}
