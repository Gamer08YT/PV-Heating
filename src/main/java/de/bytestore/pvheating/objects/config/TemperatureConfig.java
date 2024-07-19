package de.bytestore.pvheating.objects.config;

import de.bytestore.pvheating.entity.SensorType;
import lombok.Data;

@Data
public class TemperatureConfig {
    private SensorType sensorType = SensorType.NTC;

    private long resistance = 10000;

    private long desiredTemperature = 60;
}
