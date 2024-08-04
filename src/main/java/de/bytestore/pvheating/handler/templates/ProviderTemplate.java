package de.bytestore.pvheating.handler.templates;

import de.bytestore.pvheating.entity.GPIOChannelType;
import de.bytestore.pvheating.entity.GPIOType;
import de.bytestore.pvheating.handler.interfaces.ProviderTemplateInterface;

public class ProviderTemplate implements ProviderTemplateInterface {
    @Override
    public String name() {
        return "";
    }

    @Override
    public GPIOType[] type() {
        return new GPIOType[0];
    }

    @Override
    public GPIOChannelType channelType() {
        return null;
    }
}
