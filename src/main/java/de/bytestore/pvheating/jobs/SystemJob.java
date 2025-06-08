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
import org.apache.commons.numbers.core.Precision;
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
        if (config != null) {
            // Handle Smart Meter via Home Assistant.
            this.handleHomeAssistant();

            // Read Power from Lokal Power Sensor.
            this.readLocalPower();

            // Calculate Usable Power.
            this.calcUsablePower();
        }
    }

    /**
     * Calculates the usable power and stores it in a local variable.
     *
     * This method retrieves the current power value from the cache using the
     * key "current-power" and a default value of 0 if the key is not present.
     * It then utilizes the offset power and minimum power threshold configuration
     * values to compute the usable power by invoking the static
     * {@code calculateUsablePower} method.
     *
     * The calculated usable power value is stored in a local variable.
     *
     * Note: This method relies on the configuration settings and
     * the {@code CacheHandler} for proper execution.
     */
    private void calcUsablePower() {
        // Calculate Usable Power.
        double usablePower = calculateUsablePower((Double) CacheHandler.getValueOrDefault("current-power", (double) 0), config.getPower().getOffsetPower(), config.getPower().getMinPower());

        // Set Cache Value for Frontend.
        CacheHandler.setValue("usable-power", Precision.round(usablePower, 2));
    }

    /**
     * Calculates the usable power based on the current power consumption, offset, and minimum power threshold.
     *
     * @param currentPower the current power consumption
     * @param offset       the offset value to be subtracted from the absolute value of currentPower
     * @param minPower     the minimum power threshold
     * @return the usable power value
     */
    public static double calculateUsablePower(double currentPower, double offset, double minPower) {
        if (currentPower < -(minPower + offset)) {
            // Falls der Stromverbrauch (Einspeisung) kleiner ist als -minPower, berechnen wir usablePower
            return Math.abs(currentPower) - offset;
        } else {
            // Andernfalls ist die nutzbare Leistung 0
            return (double) 0;
        }
    }

    /**
     * Reads the local power data and updates the cache with the heater power value.
     *
     * This method retrieves the current power value using the `getPower` method
     * and stores it in the cache under the key "heater-power". It interacts with
     * the `CacheHandler` to facilitate the caching mechanism.
     *
     * Note: This method is intended for internal use within the SystemJob class
     * and is not accessible publicly.
     */
    private void readLocalPower() {
        CacheHandler.setValue("heater-power", getPower());
    }

    /**
     * Retrieves the power value by communicating with a Modbus slave device.
     *
     * The method initializes a ModbusSlave instance with predefined communication
     * settings (baud rate, data bits, stop bits, parity, and port). It then reads
     * the power value from the Modbus register using the modbusService. The result
     * is rounded to two decimal places before being returned.
     *
     * @return the rounded power value as a double read from the Modbus device
     */
    private double getPower() {
        ModbusSlave slaveIO = new ModbusSlave();
        slaveIO.setBaud(9600);
        slaveIO.setDataBits(8);
        slaveIO.setStopBits(1);
        slaveIO.setParity(0);
        slaveIO.setPort("/dev/ttyUSB0");

        return Precision.round(((Float) modbusService.readInput(slaveIO, 1, 52, "")).doubleValue(), 2);
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
