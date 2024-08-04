package de.bytestore.pvheating.handler.interfaces;

import de.bytestore.pvheating.entity.GPIOChannelType;
import de.bytestore.pvheating.entity.GPIOType;

public interface ProviderTemplateInterface {
    String name();

    GPIOType[] type();

    GPIOChannelType channelType();

    void onInput(Object valueIO);

    String suffix();
}
