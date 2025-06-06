package de.bytestore.pvheating.jobs;

import de.bytestore.pvheating.configuration.DefaultPinout;
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
    public static boolean templock = false;
    public static int standbyCounter = 0;

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
        switch ((String) CacheHandler.getValueOrDefault("mode", "standby")) {
            case "consume":
                consumeMode();
                break;
            case "dynamic":
                dynamicMode();
                break;
        }
    }

    /**
     * Activates the dynamic mode for managing power configuration.
     * This method internally calls the {@code handlePower()} method to
     * handle specific power-related operations, such as calculating
     * usable power, adjusting PWM values, and updating cache values.
     *
     * Note: This method is a private helper invoked within the containing
     * class for managing dynamic behaviors related to the SCRJob's
     * execution.
     */
    private void dynamicMode() {
        handlePower();
    }

    private void consumeMode() {
        handleWork();
    }

    /**
     * Handles work-related operations based on the system configuration and cache state.
     *
     * This method checks if the system's configuration and power settings are available.
     * It further verifies a cache condition ("devCalibration") to control the execution flow.
     *
     * Note: The logic within this method may depend on specific cache flag settings
     * and might trigger additional behaviors in the future as part of the enclosing class's workflow.
     */
    private void handleWork() {
        if (config != null && config.getPower() != null) {
            if (!((boolean) CacheHandler.getValueOrDefault("devCalibration", false))) {

            }
        }
    }

    /**
     * Handles the management of system power and PWM (Pulse Width Modulation) based on configuration,
     * cache states, and temperature locks.
     *
     * This method retrieves the current usable power from the cache and determines whether to adjust
     * system states such as SCR and pump activation, PWM value adjustment, and cache updates.
     * It verifies whether the system is in "devCalibration" mode and processes the power configuration
     * only when this mode is disabled. The method uses the following key steps:
     *
     * - Retrieves the usable power value and validates the presence of power configuration.
     * - Adjusts the PWM value depending on the usable power and SCR type (specifically for PWM SCRs).
     * - Evaluates the power input-to-output ratio and modifies the maximum PWM value (e.g., increment or
     *   decrement based on heater power comparison).
     * - Dynamically updates SCR and pump states based on temperature conditions.
     * - Logs and sets the resulting PWM value in the service and cache for utilization by other systems.
     *
     * Note: If the system configuration or power settings are not properly initialized, this method
     * will not execute further operations. Additionally, the maximum PWM value is reset in scenarios
     * where temperature constraints are exceeded.
     */
    private void handlePower() {
        if (config != null && config.getPower() != null) {

            double usablePower = (double) CacheHandler.getValueOrDefault("usable-power", 0);

//            if(usablePower > 0 && usablePower > config.getPower().getMinPower()) {
            if (handleTemperature()) {
                if (!((boolean) CacheHandler.getValueOrDefault("devCalibration", false))) {
                    if (config.getScr().getType().equals(SCRType.PWM)) {
                        //Double pwmIO = calculatePWM(usablePower);
                        double powerIO = (double) CacheHandler.getValueOrDefault("heater-power", -1);


                        if (powerIO != -1) {
                            if (usablePower == 0) {
                                handleStandbyCounter();
                            } else {
                                // Reset Standby Counter.
                                standbyCounter = 0;

                                CacheHandler.setValue("standbyCounter", standbyCounter);

                                log.info("Resetting Standby Counter");

                                // Reenable SCR and Pump.
                                enablePumpAndSCR();
                            }

                            if (standbyCounter < 60) {
                                if (powerIO > usablePower) {
                                    if (maxPWM > 0) {
                                        maxPWM = maxPWM - 1;
                                    }

                                    //service.setSCRState(false);
                                } else if (powerIO < usablePower) {
                                    maxPWM = maxPWM + 1;

                                    service.setSCRState(true);
                                    service.setPumpState(true);
                                }
                            }

                        }


                        log.info("SET PWM TO " + maxPWM);

                        // Set PWM Value.
                        service.setPWM(DefaultPinout.SCR_PWM_GPIO, (double) maxPWM);

                        // Set Cache Value for Frontend.
                        CacheHandler.setValue("scr-pwm", maxPWM);
                    }
                }
            } else {
                maxPWM = 0;

                // Set Cache Value for Frontend.
                CacheHandler.setValue("scr-pwm", maxPWM);
            }

            //          } else {
            // Set Usable Power to 0 for Stats.
            //            usablePower = 0;
            //        }


        }
    }

    /**
     * Enables both the SCR (Silicon Controlled Rectifier) and the pump.
     *
     * This method allows power and functionality to flow through the system by:
     * - Activating the SCR using the {@code setSCRState(true)} method from the service class.
     * - Enabling the pump by invoking {@code setPumpState(true)} in the service class.
     *
     * The purpose of this method is to combine the activation of both system components,
     * making them operational simultaneously.
     */
    private void enablePumpAndSCR() {
        service.setSCRState(true);
        service.setPumpState(true);
    }

    /**
     * Manages the standby counter for the system and performs actions when the counter reaches a specific threshold.
     *
     * This method increments the standby counter if its value is less than 60. When the counter reaches 60,
     * it disables the SCR (Silicon Controlled Rectifier) and the pump by interacting with the associated service methods.
     *
     * The purpose of this method is to monitor and control the system's behavior during a standby state,
     * ensuring that the necessary components are deactivated once the threshold is reached.
     *
     * Key behaviors:
     * - Increments the `standbyCounter` until it reaches the predefined limit (60).
     * - Disables the SCR and pump when the counter equals or exceeds the limit.
     */
    private void handleStandbyCounter() {
        if (standbyCounter < 60) {
            // Increment Standby Counter.
            standbyCounter++;

            // Disable Pump and SCR on Standby.
            if (standbyCounter >= 60) {
                service.setSCRState(false);
                service.setPumpState(false);

                CacheHandler.setValue("standbyCounter", standbyCounter);

                log.info("Standby Counter Reached. SCR and Pump Disabled.");
            }
        }
    }


    /**
     * Handles the temperature lock mechanism based on predefined thresholds.
     *
     * This method interacts with the cache to retrieve the current temperature value
     * and manages the state of the temperature lock (`templock`). If the temperature exceeds
     * 55 degrees, the lock is enabled, preventing further actions. If the temperature drops below
     * 50 degrees, the lock is disabled, allowing further actions. The temperature is retrieved
     * from the cache, with a default value of 55.0 degrees if no value is present.
     *
     * @return true if the temperature lock is inactive or disabled, false when the lock becomes active
     */
    private boolean handleTemperature() {
        if (!templock) {
            if ((Double) CacheHandler.getValueOrDefault("temperature", 55.0) > 55) {
                // Enable Templock.
                SCRJob.templock = true;

                return false;
            }
        } else {
            if ((Double) CacheHandler.getValueOrDefault("temperature", 55.0) < 50) {
                // Disable Templock.
                SCRJob.templock = false;

                return true;
            }

            return false;
        }

        return true;
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


}
