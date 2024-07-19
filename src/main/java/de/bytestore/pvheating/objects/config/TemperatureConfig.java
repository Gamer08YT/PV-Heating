package de.bytestore.pvheating.objects.config;

import de.bytestore.pvheating.entity.SensorType;
import lombok.Data;

@Data
public class TemperatureConfig {
    private SensorType sensorType = SensorType.NTC;

    private double resistance = 10000;

    private double desiredTemperature = 60;
}
