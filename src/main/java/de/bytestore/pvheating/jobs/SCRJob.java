package de.bytestore.pvheating.jobs;

import de.bytestore.pvheating.handler.CacheHandler;
import de.bytestore.pvheating.handler.ConfigHandler;
import de.bytestore.pvheating.objects.config.system.SystemConfig;
import de.bytestore.pvheating.service.Pi4JService;
import io.jmix.core.security.Authenticated;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class SCRJob implements Job {
    @Autowired
    public Pi4JService service;

    private SystemConfig config = ConfigHandler.getCached();

    @Authenticated
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        this.handlePower();
    }

    private void handlePower() {
        // Calculate Usable Power.
        double usablePower = this.calculateUsablePower((Double) CacheHandler.getValue("current-power"), config.getPower().getOffsetPower(), config.getPower().getMinPower());

        // Set PWM Value.
        service.setPWM(19, calculatePWM(usablePower));

        // Set Cache Value for Frontend.
        CacheHandler.setValue("usable-power", usablePower);
    }

    /**
     * Calculates the PWM (Pulse Width Modulation) value based on the usable power.
     *
     * @param usablePower the usable power value
     * @return the PWM value
     */
    private Double calculatePWM(double usablePower) {
        double maxPWM = config.getScr().getMaxPWM() - config.getScr().getMinPWM();

        return (maxPWM / config.getPower().getMaxPower()) * usablePower;
    }

    /**
     * Calculates the usable power based on the current power consumption, offset, and minimum power threshold.
     *
     * @param currentPower the current power consumption
     * @param offset the offset value to be subtracted from the absolute value of currentPower
     * @param minPower the minimum power threshold
     * @return the usable power value
     */
    public static double calculateUsablePower(double currentPower, double offset, double minPower) {
        if (currentPower < -minPower) {
            // Falls der Stromverbrauch (Einspeisung) kleiner ist als -minPower, berechnen wir usablePower
            return Math.abs(currentPower) - offset;
        } else {
            // Andernfalls ist die nutzbare Leistung 0
            return 0;
        }
    }
}
