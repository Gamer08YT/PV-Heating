package de.bytestore.pvheating.objects.config;

import lombok.Data;

@Data
public class SystemConfig {
    public int keepStatsInDays = 30;

    public TemperatureConfig temperature = new TemperatureConfig();

    public PowerConfig power = new PowerConfig();

    public SCRConfig scr = new SCRConfig();

    public PumpConfig pump = new PumpConfig();

}
