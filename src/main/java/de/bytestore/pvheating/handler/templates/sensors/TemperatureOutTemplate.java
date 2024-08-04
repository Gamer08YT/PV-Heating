package de.bytestore.pvheating.handler.templates.sensors;

import org.springframework.stereotype.Component;

@Component
public class TemperatureOutTemplate extends TemperatureInTemplate {
    @Override
    public String name() {
        return "temperature-out";
    }
}
