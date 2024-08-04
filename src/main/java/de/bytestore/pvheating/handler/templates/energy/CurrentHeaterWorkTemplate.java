package de.bytestore.pvheating.handler.templates.energy;

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
