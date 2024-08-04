package de.bytestore.pvheating.handler.templates.outputs;

import de.bytestore.pvheating.entity.GPIOChannelType;
import de.bytestore.pvheating.entity.GPIOType;
import de.bytestore.pvheating.handler.templates.ProviderTemplate;
import org.springframework.stereotype.Component;

@Component
public class PumpTemplate extends ProviderTemplate {
    @Override
    public String name() {
        return "pump";
    }

    @Override
    public GPIOType[] type() {
        return new GPIOType[] {GPIOType.DIGITAL};
    }

    @Override
    public GPIOChannelType channelType() {
        return GPIOChannelType.OUTPUT;
    }
}
