package de.bytestore.pvheating.handler.templates.status;

import org.springframework.stereotype.Component;

@Component
public class ButtonOperationTemplate extends ButtonResetTemplate {
    @Override
    public String name() {
        return "button-operation";
    }


}
