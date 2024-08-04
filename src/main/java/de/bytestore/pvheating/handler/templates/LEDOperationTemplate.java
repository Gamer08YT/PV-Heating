package de.bytestore.pvheating.handler.templates;

import de.bytestore.pvheating.entity.GPIOChannelType;
import de.bytestore.pvheating.entity.GPIOType;

public class LEDOperationTemplate extends ProviderTemplate {
    @Override
    public String name() {
        return "led-operation";
    }

    @Override
    public GPIOType[] type() {
        return new GPIOType[] {GPIOType.DIGITAL, GPIOType.PWM};
    }

    @Override
    public GPIOChannelType channelType() {
        return GPIOChannelType.OUTPUT;
    }


}
