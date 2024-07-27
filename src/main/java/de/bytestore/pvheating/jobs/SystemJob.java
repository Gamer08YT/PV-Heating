package de.bytestore.pvheating.jobs;

import com.google.gson.JsonObject;
import com.pi4j.io.gpio.digital.DigitalInput;
import de.bytestore.pvheating.handler.CacheHandler;
import de.bytestore.pvheating.handler.HAHandler;
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
    private static final int PULSES_PER_LITER = 450;

    // Pulse Snapshot Time.
    private static final long INTERVAL_S = 1;

    private static DigitalInput listener;

    private static long startTime;

    @Authenticated
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // Handle Smart Meter via Home Assistant.
        this.handleHomeAssistant();

        // Read Flow Sensor.
        this.readFlowSensor();
    }



    /**
     *
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
        double flowRate = (GPIOService.getFLOW_PULSE_COUNT() / (double) PULSES_PER_LITER) / (elapsedTime / 60000.0);

        // reset pulse count and start time
        GPIOService.setFLOW_PULSE_COUNT(0);
        startTime = currentTime;

        // print the flow rate
        System.out.printf("Flow Rate: %.2f L/min%n", flowRate);

        return flowRate;
    }

    /**
     *
     */
    private void handleHomeAssistant() {
        JsonObject powerIO = HAHandler.getSensorState("sensor.derzeitige_wirkleistung");

        // Check if Power is available.
        if(powerIO.has("state")) {
            CacheHandler.setValue("current-power", powerIO.get("state").getAsDouble());
        }
    }
}
