package de.bytestore.pvheating.handler.templates;

import de.bytestore.pvheating.entity.GPIOChannelType;
import de.bytestore.pvheating.entity.GPIOType;

public class ButtonOperationTemplate extends ButtonResetTemplate {
    @Override
    public String name() {
        return "button-operation";
    }


}
