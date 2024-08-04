package de.bytestore.pvheating.handler.templates.sensors;

import org.springframework.stereotype.Component;

@Component
public class TemperatureBoilerTemplate extends TemperatureInTemplate {
    @Override
    public String name() {
        return "temperature-boiler";
    }
}
