package de.bytestore.pvheating.jobs;

import com.google.gson.JsonObject;
import com.pi4j.io.gpio.digital.DigitalInput;
import de.bytestore.pvheating.entity.SensorType;
import de.bytestore.pvheating.handler.CacheHandler;
import de.bytestore.pvheating.handler.ConfigHandler;
import de.bytestore.pvheating.handler.HAHandler;
import de.bytestore.pvheating.objects.config.system.SystemConfig;
import de.bytestore.pvheating.service.GPIOService;
import de.bytestore.pvheating.service.Pi4JService;
import io.jmix.core.security.Authenticated;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class SystemJob implements Job {
    @Autowired
    public Pi4JService service;

    // Pulse Steps wich equals one Liter.
    private static final double ONE_PULSE_IN_LITER = 0.002222222;

    // Pulse Snapshot Time.
    private static final long INTERVAL_S = 1;

    private static DigitalInput listener;

    private static long startTime;

    private SystemConfig config = ConfigHandler.getCached();

    @Authenticated
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // Handle Smart Meter via Home Assistant.
        this.handleHomeAssistant();

        // Read Flow Sensor.
        this.readFlowSensor();
        
        // Read Temperature Sensor.
        this.readTemperatureSensor();
    }

    /**
     * Reads the temperature from a temperature sensor based on the configuration.
     * If the configuration is not null and the sensor type is DS18B20, the temperature
     * value is read from the sensor and stored in the cache with the key "temperature".
     */
    private void readTemperatureSensor() {
        if(config != null) {
            if (config.getTemperature().getSensorType().equals(SensorType.DS18B20)) {
                CacheHandler.setValue("temperature", service.readDS18B20(config.getTemperature().getWire1Device()));
            }
        }
    }


    /**
     * Reads the flow sensor and updates the flow rate values in the cache.
     * It calculates the flow rate using the number of flow pulses and the number of pulses per liter.
     * The flow rate is then stored in the cache with keys "flow-per-second" (flow rate in liters per second)
     * and "flow-per-minute" (flow rate in liters per minute).
     * The pulse counter is reset after reading the flow sensor.
     */
    private void readFlowSensor() {
        double flowIO = calculateFlowRate();

        CacheHandler.setValue("flow-per-second", flowIO / 60);
        CacheHandler.setValue("flow-per-minute", flowIO);

        // Reset Pulse Counter.
        GPIOService.setFLOW_PULSE_COUNT(0);
    }

    /**
     * Calculates the flow rate based on the number of flow pulses
     * and the number of pulses per liter.
     *
     * @return the calculated flow rate as a double value
     */
    private double calculateFlowRate() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime; // elapsed time in milliseconds

        // calculate flow rate in liters per minute
        double flowRate = (GPIOService.getFLOW_PULSE_COUNT() * ONE_PULSE_IN_LITER);

        // reset pulse count and start time
        GPIOService.setFLOW_PULSE_COUNT(0);
        startTime = currentTime;

        return flowRate;
    }

    /**
     * Handles communication with the Home Assistant system.
     *
     * This method retrieves the current power sensor state from the Home Assistant system and caches it if available.
     * If the power state is available, it is stored in the cache with the key "current-power".
     */
    private void handleHomeAssistant() {
        JsonObject powerIO = HAHandler.getSensorState("sensor.derzeitige_wirkleistung");

        // Check if Power is available.
        if(powerIO.has("state")) {
            CacheHandler.setValue("current-power", powerIO.get("state").getAsDouble());
        }
    }
}
