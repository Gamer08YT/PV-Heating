package de.bytestore.pvheating.jobs;

import de.bytestore.pvheating.configuration.DefaultPinout;
import de.bytestore.pvheating.handler.CacheHandler;
import de.bytestore.pvheating.service.Pi4JService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * The ButtonJob class implements the Job interface and manages the behavior of GPIO-controlled
 * buttons and LEDs in response to system states such as errors, warnings, and standby modes.
 *
 * This class interacts with hardware using the Pi4J library and is responsible for:
 * - Adjusting LED brightness using PWM (Pulse Width Modulation) signals.
 * - Scheduling periodic tasks to handle the toggling of LEDs for error, warning, and status indicators.
 * - Monitoring and responding to system states stored in a shared cache.
 *
 * Tasks such as adjusting LED brightness or toggling states are executed at fixed intervals managed
 * by Java TimerTask objects.
 *
 * Core responsibilities include:
 * - Managing the "status" LED behavior in standby mode.
 * - Oscillating the "warning" LED brightness in case of warnings.
 * - Toggling the LEDs for signaling errors.
 */
@Slf4j
public class ButtonJob implements Job {
    // Error Task
    private static TimerTask errorTask;
    private boolean errorState = false;

    // Warning Task
    private static TimerTask warningTask;
    private int warningState = 0;
    private boolean warningDirection = false;

    // Status Task
    private static TimerTask statusTask;
    private int statusState = 0;
    private boolean statusDirection = false;

    private static Timer timer = new Timer();

    @Autowired
    private Pi4JService pi4j;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//        handleErrorTask();
//        handleWarningTask();
//        handleStatusTask();
    }

    /**
     * Manages the status task functionality based on the current cache state.
     *
     * If the "mode" in the cache is set to "standby", this method initializes and schedules a timer task
     * to cyclically adjust the status state, controlling the PWM signal of the status button GPIO pin.
     * The status task adjusts the brightness of the LED by incrementing or decrementing the PWM value
     * between 0 and 100, causing the LED brightness to oscillate.
     *
     * If the*/
    private void handleStatusTask() {
        if (CacheHandler.getValueOrDefault("mode", "standby").equals("standby")) {
            if (statusTask == null) {
                statusTask = new TimerTask() {

                    /**
                     * The action to be performed by this timer task.
                     */
                    @Override
                    public void run() {
                        if (statusState == 100) {
                            statusDirection = true;
                        }

                        if (statusState == 0) {
                            statusDirection = false;
                        }

                        if (!statusDirection) {
                            statusState++;
                        } else {
                            statusState--;
                        }

                        pi4j.setPWM(DefaultPinout.STATUS_BUTTON_PWM_GPIO, (double) statusState);
                    }
                };

                // Schedule every 250ms.
                timer.scheduleAtFixedRate(statusTask, 250, 250);

                log.info("Created and scheduled Timer Task for Status Button.");
            }
        } else {
            if (statusTask != null) {
                statusTask.cancel();
                statusTask = null;
                statusState = 0;
            }

            if (statusState == 0) {
                statusState = 100;
                pi4j.setPWM(DefaultPinout.STATUS_BUTTON_PWM_GPIO, (double) statusState);
            }
        }
    }

    /**
     * Manages the warning task functionality based on the current cache state.
     *
     * If the "warning" state in the cache is set to true, this method initializes and schedules a timer task
     * to cyclically adjust the warning state, controlling the PWM signal of the fault button GPIO pin.
     * The warning task adjusts the brightness of the LED by incrementing or decrementing the PWM value
     * between 0 and 100. The LED brightness oscillates to signal a warning.
     *
     * If the "warning" state in the cache is set to false, the method cancels the existing warning task,
     * resets the error state to false, and disables the error LED by setting its PWM value to 0.
     */
    private void handleWarningTask() {
        if ((boolean) CacheHandler.getValueOrDefault("warning", false)) {
            if (warningTask == null) {
                warningTask = new TimerTask() {

                    /**
                     * The action to be performed by this timer task.
                     */
                    @Override
                    public void run() {
                        if (warningState == 100) {
                            warningDirection = true;
                        }

                        if (warningState == 0) {
                            warningDirection = false;
                        }

                        if (!warningDirection) {
                            warningState++;
                        } else {
                            warningState--;
                        }

                        pi4j.setPWM(DefaultPinout.FAULT_BUTTON_PWM_GPIO, (double) warningState);
                    }
                };

                // Schedule every 250ms.
                timer.scheduleAtFixedRate(warningTask, 250, 250);

                log.info("Created and scheduled Timer Task for Warning Button.");
            }
        } else {
            if (warningTask != null) {
                warningTask.cancel();
                warningTask = null;
                errorState = false;

                disableErrorLed();
            }
        }
    }

    /**
     * Disables the error LED by setting the PWM (Pulse Width Modulation) value
     * of the fault button GPIO pin to 0.0, effectively turning off the LED.
     *
     * This method uses the pi4j library to interact with the hardware pin defined
     * by DefaultPinout.FAULT_BUTTON_PWM_GPIO.
     */
    private void disableErrorLed() {
        pi4j.setPWM(DefaultPinout.FAULT_BUTTON_PWM_GPIO, 0.0);
    }

    /**
     * Handles the error task by monitoring the error state and toggling the PWM signal
     * on the SCR pin. This method uses a timer task to periodically adjust the PWM signal
     **/
    private void handleErrorTask() {
        if ((boolean) CacheHandler.getValueOrDefault("error", false)) {
            if (errorTask == null) {
                errorTask = new TimerTask() {

                    /**
                     * The action to be performed by this timer task.
                     */
                    @Override
                    public void run() {
                        if (!errorState) {
                            errorState = true;
                            pi4j.setPWM(DefaultPinout.FAULT_BUTTON_PWM_GPIO, 100.0);
                        } else {
                            errorState = false;
                            pi4j.setPWM(DefaultPinout.FAULT_BUTTON_PWM_GPIO, 0.0);
                        }
                    }
                };

                // Schedule every 500ms.
                timer.scheduleAtFixedRate(errorTask, TimeUnit.MICROSECONDS.toMillis(1000), TimeUnit.MICROSECONDS.toMillis(1000));
            }
        } else {
            if (errorTask != null) {
                errorTask.cancel();
                errorTask = null;
                errorState = false;

                disableErrorLed();
            }
        }
    }
}
