package de.bytestore.pvheating.objects.config.system;

import de.bytestore.pvheating.entity.SensorType;
import lombok.Data;

@Data
public class TemperatureConfig {
    private boolean hasSensor = true;

    private SensorType sensorType = SensorType.NTC;

    private double resistance = 10000;

    private double desiredTemperature = 60;

    private String wire1Device = "";
}
