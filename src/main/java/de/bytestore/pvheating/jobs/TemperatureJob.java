package de.bytestore.pvheating.jobs;

import de.bytestore.pvheating.entity.SensorType;
import de.bytestore.pvheating.handler.CacheHandler;
import de.bytestore.pvheating.handler.ConfigHandler;
import de.bytestore.pvheating.objects.config.system.SystemConfig;
import de.bytestore.pvheating.service.GPIOService;
import de.bytestore.pvheating.service.Pi4JService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.numbers.core.Precision;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class TemperatureJob implements Job {
    private SystemConfig config = ConfigHandler.getCached();

    @Autowired
    public Pi4JService service;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // Read Temperature Sensor.
        this.readTemperatureSensor();
    }

    /**
     * Reads the temperature from a temperature sensor based on the configuration.
     * If the configuration is not null and the sensor type is DS18B20, the temperature
     * value is read from the sensor and stored in the cache with the key "temperature".
     */
    private void readTemperatureSensor() {
        if (config != null) {
            if (config.getTemperature().getSensorType().equals(SensorType.DS18B20)) {
                double temperatureIO = Precision.round(service.readDS18B20(config.getTemperature().getWire1Device()), 2);

                CacheHandler.setValue("temperature", temperatureIO);

                // Trigger Temperature Limit Error.
                if (temperatureIO > 60) {
                    service.triggerError("Temperature limit reached.", 20);
                }
            }
        }
    }
}
