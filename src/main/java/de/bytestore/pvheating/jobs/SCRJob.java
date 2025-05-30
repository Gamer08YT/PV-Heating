package de.bytestore.pvheating.jobs;

import de.bytestore.pvheating.configuration.DefaultPinout;
import de.bytestore.pvheating.entity.ModbusSlave;
import de.bytestore.pvheating.entity.SCRType;
import de.bytestore.pvheating.handler.CacheHandler;
import de.bytestore.pvheating.handler.ConfigHandler;
import de.bytestore.pvheating.objects.config.system.SystemConfig;
import de.bytestore.pvheating.service.ModbusService;
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

    @Autowired
    public ModbusService modbusService;

    private SystemConfig config = ConfigHandler.getCached();

    public static int maxPWM = 0;

    @Authenticated
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //this.handlePower();
    }

    /**
     * Handles the power based on the system configuration.
     * If a configuration is set, it calculates the usable power and performs the corresponding actions:
     * - If the SCR type is PWM, it sets the PWM value.
     * - It sets the cache value for frontend.
     * <p>
     * Note: This method is private and is called internally by the execute() method of the SCRJob class.
     */
    private void handlePower() {
        if (config != null && config.getPower() != null) {
            // Calculate Usable Power.
            double usablePower = calculateUsablePower((Double) CacheHandler.getValueOrDefault("current-power", (double) 0), config.getPower().getOffsetPower(), config.getPower().getMinPower());

//            if(usablePower > 0 && usablePower > config.getPower().getMinPower()) {
            if (!((boolean) CacheHandler.getValueOrDefault("devCalibration", false))) {
                if (config.getScr().getType().equals(SCRType.PWM)) {
                    Double pwmIO = calculatePWM(usablePower);
                    double powerIO = (double) CacheHandler.getValueOrDefault("heater-power", -1);

                    if (powerIO != -1) {
                        if (powerIO > usablePower) {
                            if (maxPWM > 0) {
                                maxPWM = maxPWM - 1;
                            }

                            service.setSCRState(false);
                        } else if (powerIO < usablePower) {
                            maxPWM = maxPWM + 1;

                            service.setSCRState(true);
                            service.setPumpState(true);
                        }


                        log.info("SET PWM TO " + maxPWM);

                        // Set PWM Value.
                        service.setPWM(DefaultPinout.SCR_PWM_GPIO, (double) maxPWM);

                        // Set Cache Value for Frontend.
                        CacheHandler.setValue("scr-pwm", pwmIO);

                    }
                }
            }

            //          } else {
            // Set Usable Power to 0 for Stats.
            //            usablePower = 0;
            //        }

            // Set Cache Value for Frontend.
            CacheHandler.setValue("usable-power", usablePower);
        }
    }


    /**
     * Calculates the PWM (Pulse Width Modulation) value based on the usable power.
     *
     * @param usablePower the usable power value
     * @return the PWM value
     */
    private Double calculatePWM(double usablePower) {
        double maxPWM = config.getScr().getMaxPWM() - config.getScr().getMinPWM();

        return Math.round(maxPWM / config.getPower().getMaxPower()) * usablePower;
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


}
