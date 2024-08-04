package de.bytestore.pvheating.handler.templates.status;

import org.springframework.stereotype.Component;

@Component
public class LEDErrorTemplate extends LEDOperationTemplate {
    @Override
    public String name() {
        return "led-erro";
    }



}
