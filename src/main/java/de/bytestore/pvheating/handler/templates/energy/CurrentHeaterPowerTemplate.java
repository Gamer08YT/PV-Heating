package de.bytestore.pvheating.handler.templates.energy;

import org.springframework.stereotype.Component;

@Component
public class CurrentHeaterPowerTemplate extends CurrentPowerTemplate {
    @Override
    public String name() {
        return "current-heater-power-meter";
    }

}
