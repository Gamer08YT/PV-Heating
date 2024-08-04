package de.bytestore.pvheating.handler.templates.energy;

import org.springframework.stereotype.Component;

@Component
public class CurrentHeaterWorkTemplate extends CurrentPowerTemplate {
    @Override
    public String name() {
        return "current-heater-work-meter";
    }

    @Override
    public String suffix() {
        return "kWh";
    }
}
