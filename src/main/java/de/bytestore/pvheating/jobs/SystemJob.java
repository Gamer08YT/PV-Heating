package de.bytestore.pvheating.jobs;

import com.google.gson.JsonObject;
import com.pi4j.io.gpio.digital.DigitalInput;
import de.bytestore.pvheating.entity.ModbusSlave;
import de.bytestore.pvheating.entity.SensorType;
import de.bytestore.pvheating.handler.CacheHandler;
import de.bytestore.pvheating.handler.ConfigHandler;
import de.bytestore.pvheating.handler.HAHandler;
import de.bytestore.pvheating.objects.config.system.SystemConfig;
import de.bytestore.pvheating.service.GPIOService;
import de.bytestore.pvheating.service.ModbusService;
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

    @Autowired
    private ModbusService modbusService;

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

        // Read Power from Lokal Power Sensor.
        this.readLocalPower();
    }

    private void readLocalPower() {
        CacheHandler.setValue("heater-power", getPower());
    }

    private double getPower() {
        ModbusSlave slaveIO = new ModbusSlave();
        slaveIO.setBaud(9600);
        slaveIO.setDataBits(8);
        slaveIO.setStopBits(1);
        slaveIO.setParity(0);
        slaveIO.setPort("/dev/ttyUSB0");

        return ((Float) modbusService.readInput(slaveIO, 1, 52, "")).doubleValue();
    }



    /**
     * Handles communication with the Home Assistant system.
     * <p>
     * This method retrieves the current power sensor state from the Home Assistant system and caches it if available.
     * If the power state is available, it is stored in the cache with the key "current-power".
     */
    private void handleHomeAssistant() {
        JsonObject powerIO = HAHandler.getSensorState("sensor.derzeitige_wirkleistung");

        // Check if Power is available.
        if (powerIO.has("state") && (CacheHandler.getValueOrDefault("devPowerOverride", false).equals(false))) {
            CacheHandler.setValue("current-power", powerIO.get("state").getAsDouble());
        }
    }
}
