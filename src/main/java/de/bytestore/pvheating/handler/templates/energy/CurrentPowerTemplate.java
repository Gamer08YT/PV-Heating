package de.bytestore.pvheating.handler.templates.energy;

import de.bytestore.pvheating.entity.GPIOChannelType;
import de.bytestore.pvheating.entity.GPIOType;
import de.bytestore.pvheating.handler.templates.ProviderTemplate;

public class CurrentPowerTemplate extends ProviderTemplate {
    @Override
    public String name() {
        return "current-power-meter";
    }

    @Override
    public GPIOType[] type() {
        return new GPIOType[] {GPIOType.MODBUS};
    }

    @Override
    public GPIOChannelType channelType() {
        return GPIOChannelType.INPUT;
    }

    @Override
    public String suffix() {
        return "W";
    }
}
