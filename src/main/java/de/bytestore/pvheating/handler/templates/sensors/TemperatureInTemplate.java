package de.bytestore.pvheating.handler.templates.sensors;

import de.bytestore.pvheating.entity.GPIOChannelType;
import de.bytestore.pvheating.entity.GPIOType;
import de.bytestore.pvheating.handler.templates.ProviderTemplate;
import org.springframework.stereotype.Component;

@Component
public class TemperatureInTemplate extends ProviderTemplate {
    @Override
    public String name() {
        return "temperature-in";
    }

    @Override
    public GPIOType[] type() {
        return new GPIOType[]{GPIOType.ANALOG, GPIOType.MODBUS, GPIOType.WIRE1, GPIOType.HOMEASSISTANT};
    }

    @Override
    public GPIOChannelType channelType() {
        return GPIOChannelType.INPUT;
    }

    @Override
    public String suffix() {
        return "°C";
    }
}
