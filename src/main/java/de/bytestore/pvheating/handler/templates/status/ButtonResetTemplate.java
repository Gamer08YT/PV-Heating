package de.bytestore.pvheating.handler.templates.status;

import de.bytestore.pvheating.entity.GPIOChannelType;
import de.bytestore.pvheating.entity.GPIOType;
import de.bytestore.pvheating.handler.templates.ProviderTemplate;
import org.springframework.stereotype.Component;

@Component
public class ButtonResetTemplate extends ProviderTemplate {
    @Override
    public String name() {
        return "button-reset";
    }

    @Override
    public GPIOType[] type() {
        return new GPIOType[] {GPIOType.DIGITAL};
    }

    @Override
    public GPIOChannelType channelType() {
        return GPIOChannelType.INPUT;
    }
}
