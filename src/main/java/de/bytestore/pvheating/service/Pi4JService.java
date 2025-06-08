package de.bytestore.pvheating.service;

import com.pi4j.boardinfo.definition.BoardModel;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmType;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalOutput;
import de.bytestore.pvheating.configuration.DefaultPinout;
import de.bytestore.pvheating.handler.CacheHandler;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.pi4j.io.gpio.digital.DigitalState.*;

@Service
public class Pi4JService {
    private static final Logger log = LoggerFactory.getLogger(Pi4JService.class);
    private final Context pi4jContext;

    @Getter
    private int wire1fails = 0;

    // Store already registered GPIO pins.
    private static HashMap<Integer, Object> provider = new HashMap<Integer, Object>();
    private static final String W1_DEVICE_FOLDER = "/sys/bus/w1/devices/";

    @Autowired
    public Pi4JService(@Autowired Context pi4jContext) {
        this.pi4jContext = pi4jContext;

        this.setDefaultStates();
    }

    /**
     * Sets the state of a pin based on the given pin number and state.
     *
     * @param pinIO   The pin number to set the state for.
     * @param stateIO The state to set for the pin (true for HIGH, false for LOW).
     */
    public void setPinState(int pinIO, boolean stateIO) {
        DigitalOutput digitalOutput = null;

        if (!Pi4JService.provider.containsKey(pinIO)) {
            digitalOutput = pi4jContext.digitalOutput().create(pinIO);

            // Put Provider to Cache.
            Pi4JService.provider.put(pinIO, digitalOutput);
        } else
            digitalOutput = (DigitalOutput) Pi4JService.provider.get(pinIO);

        digitalOutput.state(DigitalState.getState(stateIO));

        log.debug("Set Pin {} to {}.", pinIO, stateIO);
    }

    /**
     * Retrieves the current state of a specified pin.
     *
     * @param pinIO The pin number to retrieve the state for.
     * @return true if the pin state is high, false if the pin state is low.
     */
    public boolean getPinState(int pinIO) {
        DigitalInput digitalInput = null;

        if (!Pi4JService.provider.containsKey(pinIO)) {
            digitalInput = pi4jContext.digitalInput().create(pinIO);

            // Put Provider to Cache.
            Pi4JService.provider.put(pinIO, digitalInput);
        } else
            digitalInput = (DigitalInput) Pi4JService.provider.get(pinIO);

        return digitalInput.state() == HIGH;
    }

    /**
     * Reads the temperature value from a DS18B20 sensor connected to a 1-Wire bus.
     *
     * @param deviceIO The name of the 1-Wire device folder containing the DS18B20 sensor.
     * @return The temperature value in Celsius read from the DS18B20 sensor.
     * Returns -1 if there was an error reading the temperature.
     */
    public double readDS18B20(String deviceIO) {
        List<String> lines = get1Wire(deviceIO, "w1_slave");

        if (lines.size() > 0) {
            if (lines.get(0).contains("YES")) {

                if (lines.get(0).contains("YES")) {
                    String tempLine = lines.get(1);
                    int tIndex = tempLine.indexOf("t=");

                    if (tIndex != -1) {
                        String tempString = tempLine.substring(tIndex + 2);

                        return Double.parseDouble(tempString) / 1000.0;
                    }
                }
            } else {
                log.error("Error reading temperature for DS18B20 {}.", deviceIO);
            }
        }

        return -1;
    }

    /**
     * Retrieves the contents of a specified 1-Wire file.
     *
     * @param deviceIO The name of the 1-Wire device folder.
     * @param dirIO    The name of the 1-Wire file within the device folder.
     * @return A list of strings representing the contents of the specified 1-Wire file.
     * @throws RuntimeException if an I/O error occurs while reading the file.
     */
    public List<String> get1Wire(String deviceIO, String dirIO) {
        if (wire1fails < 3) {
            try {
                return Files.readAllLines(new File(W1_DEVICE_FOLDER + deviceIO + "/" + dirIO).toPath());
            } catch (IOException e) {
                wire1fails++;

                throw new RuntimeException(e);
            }
        }

        return new ArrayList<String>();
    }

    /**
     * Retrieves a list of 1-Wire devices connected to the system.
     *
     * @return A list of strings representing the names of the 1-Wire devices.
     * @throws RuntimeException if an I/O error occurs while retrieving the list of devices.
     */
    public List<String> get1WireDevices() {
        try {
            return Files.list(new File(W1_DEVICE_FOLDER).toPath())
                    .filter(Files::isDirectory)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Sets the PWM value for a specified pin.
     *
     * @param pinIO The pin number for which the PWM value should be set.
     * @param value The PWM value to be set. A positive value turns the pin on, while a negative value turns it off.
     */
    public void setPWM(int pinIO, Double value) {
        setPWM(pinIO, value, false);
    }

    /**
     * Configures the PWM (Pulse Width Modulation) settings for a specified pin.
     *
     * @param pinIO The identifier for the pin to which the PWM signal will be applied.
     * @param value The duty cycle value for the PWM signal. A value greater than 0 enables the signal;
     *              otherwise, the PWM is turned off.
     * @param normal If true, uses the default configuration for the PWM signal;
     *               otherwise, applies a custom configuration with a specified duty cycle and a fixed period.
     */
    public void setPWM(int pinIO, Double value, boolean normal) {
        Pwm pwmConfig = getPWM(pinIO);

        if (value > 0)
            if (!normal)
                pwmConfig.on(value.intValue(), 10000);
            else
                pwmConfig.on(value.intValue());
        else
            pwmConfig.off();
    }

    /**
     * Retrieves the PWM configuration for the specified pin. If the configuration is not found in the provider cache,
     * it creates a new configuration using the pi4jContext and adds it to the cache. If the configuration is already
     * present in the cache, it retrieves it from the cache and returns it.
     *
     * @param pinIO The pin number for which the PWM configuration is required.
     * @return The PWM configuration for the specified pin.
     */
    public Pwm getPWM(int pinIO) {
        Pwm pwmConfig = null;

        if (!Pi4JService.provider.containsKey(pinIO)) {
            pwmConfig = pi4jContext.create(Pwm.newConfigBuilder(pi4jContext).pwmType((pinIO == 12 || pinIO == 13 || pinIO == 18 || pinIO == 19 ? PwmType.HARDWARE : PwmType.SOFTWARE)).initial(0).shutdown(0).address(pinIO).build());

            // Put Provider to Cache.
            Pi4JService.provider.put(pinIO, pwmConfig);
        } else
            pwmConfig = (Pwm) Pi4JService.provider.get(pinIO);

        return pwmConfig;
    }


    /**
     * Retrieves the context of the Pi4J library.
     * The context provides access to various Pi4J features and functionalities.
     *
     * @return The Pi4J context.
     */
    public Context getPi4jContext() {
        return pi4jContext;
    }

    /**
     * Retrieves the board model associated with the Pi4J service.
     *
     * @return The board model.
     */
    public BoardModel getModel() {
        return pi4jContext.boardInfo().getBoardModel();
    }

    /**
     * Resets the number of failed attempts to read a specified 1-Wire file.
     * <p>
     * This method sets the wire1fails variable to 0, indicating that the subsequent read attempts for the
     * specified 1-Wire file should not be counted as failures.
     * <p>
     * Additionally, this method logs an informational message indicating that the failed attempts are being reset.
     * <p>
     * This method does not return a value.
     */
    public void resetFails() {
        wire1fails = 0;

        // Print Info about resetting failed attempts.
        log.info("Resetting failed attempts.");
    }

    /**
     * Retrieves the current state of a digital GPIO pin.
     *
     * @param pinIO The GPIO pin number to check.
     * @return true if the digital pin is in a high state, false otherwise.
     */
    public boolean getDigitalPin(int pinIO) {
        if (Pi4JService.provider.containsKey(pinIO)) {
            if (Pi4JService.provider.get(pinIO) instanceof PiGpioDigitalOutput) {
                return ((PiGpioDigitalOutput) Pi4JService.provider.get(pinIO)).isHigh();
            }
        }

        return false;
    }

    /**
     * Resets the system states to their default values.
     * This method is used to initialize or reset the state of the system by setting
     * specific components to their default state. Specifically, it ensures that the
     * SCR state and the pump state are both set to false.
     */
    public void setDefaultStates() {
        CacheHandler.setValue("mode", "standby");

        this.setSCRState(false);
        this.setPumpState(false);

    }

    /**
     * Sets the state of the pump by controlling the digital output on GPIO Pin 16.
     * If the current state differs from the desired state, the method will toggle the pin's state.
     * A log message will be generated to indicate the updated pump state.
     *
     * @param stateIO the desired state of the pump.
     *        True represents the pump being on, and false represents the pump being off.
     */
    public void setPumpState(boolean stateIO) {
        //if (getDigitalPin(DefaultPinout.PUMP_ENABLE_GPIO) == stateIO) {
        setPinState(DefaultPinout.PUMP_ENABLE_GPIO, !stateIO);

        log.info("Set Pump State to {} on GPIO Pin " + DefaultPinout.PUMP_ENABLE_GPIO, stateIO);
        //}
    }

    /**
     * Sets the state of the SCR (Silicon Controlled Rectifier) on GPIO pin 23.
     * If the desired state differs from the current state of the pin, it toggles the pin state
     * and logs the operation.
     *
     * @param stateIO the desired state to set for the SCR; true for ON, false for OFF
     */
    public void setSCRState(boolean stateIO) {
        //if (getDigitalPin(DefaultPinout.SCR_ENABLE_GPIO) == stateIO) {
        setPinState(DefaultPinout.SCR_ENABLE_GPIO, !stateIO);

        // Set PWM of SCR to Off.
        if (!stateIO)
            setPWM(DefaultPinout.SCR_PWM_GPIO, 0.00);

        log.info("Set SCR State to {} on GPIO Pin " + DefaultPinout.SCR_ENABLE_GPIO, stateIO);
        //}
    }

    /**
     * Sets the error status for the system by updating the state of a specified GPIO pin.
     *
     * @param stateIO the desired error status to set. If true, it indicates an error state;
     *                if false, it indicates a non-error state.
     */
    public void setErrorStatus(boolean stateIO) {
        if (getPWM(DefaultPinout.FAULT_BUTTON_PWM_GPIO).isOn() != stateIO) {
            setPWM(DefaultPinout.FAULT_BUTTON_PWM_GPIO, Double.valueOf((stateIO ? 100 : 0)));

            log.info("Set Error State to {} on GPIO Pin " + DefaultPinout.FAULT_BUTTON_PWM_GPIO, stateIO);
        }
    }

    /**
     * Sets the enable status of the status button based on the specified state.
     *
     * @param stateIO a boolean value representing the desired status.
     *                If true, the status button will be enabled. If false, it will be disabled.
     */
    public void setEnableStatus(boolean stateIO) {
        if (getPWM(DefaultPinout.STATUS_BUTTON_PWM_GPIO).isOn() != stateIO) {
            setPWM(DefaultPinout.STATUS_BUTTON_PWM_GPIO, Double.valueOf((stateIO ? 100 : 0)));

            log.info("Set Status State to {} on GPIO Pin " + DefaultPinout.STATUS_BUTTON_PWM_GPIO, stateIO);
        }
    }

    /**
     * Checks if the SCR (Silicon Controlled Rectifier) is enabled by reading the state of the designated GPIO pin.
     *
     * @return true if SCR is enabled; false otherwise.
     */
    public Boolean isSCREnabled() {
        return !getDigitalPin(DefaultPinout.SCR_ENABLE_GPIO);
    }

    /**
     * Checks whether the pump is currently enabled.
     *
     * @return true if the pump is enabled, otherwise false
     */
    public Boolean isPumpEnabled() {
        return !getDigitalPin(DefaultPinout.PUMP_ENABLE_GPIO);
    }

    /**
     * Triggers an error based on the specified reason and code.
     *
     * @param reasonIO the reason for the error
     * @param codeIO the error code associated with the error
     */
    public void triggerError(String reasonIO, int codeIO) {
        this.triggerException(reasonIO, codeIO, false);
    }

    /**
     * Triggers a warning based on the provided reason and code.
     *
     * @param reasonIO the reason for triggering the warning
     * @param codeIO the specific code associated with the warning
     */
    public void triggerWarning(String reasonIO, int codeIO) {
        this.triggerException(reasonIO, codeIO, true);
    }


    /**
     * Triggers an exception or warning based on the provided parameters and updates the cache handler accordingly.
     *
     * @param reasonIO The reason for the exception or warning being triggered.
     * @param codeIO The code associated with the exception or warning.
     * @param warnIO If true, a warning is triggered; if false, an exception is triggered.
     */
    private void triggerException(String reasonIO, int codeIO, boolean warnIO) {
        if (!warnIO) {
            setDefaultStates();

            CacheHandler.setValue("error", true);
            CacheHandler.setValue("errorReason", reasonIO);
            CacheHandler.setValue("errorCode", codeIO);
        } else {
            CacheHandler.setValue("warning", true);
            CacheHandler.setValue("warningReason", reasonIO);
            CacheHandler.setValue("warningCode", codeIO);
        }

    }

    /**
     * Resets the error state in the application by clearing relevant cache entries.
     *
     * This method performs the following actions:
     * - Sets the "error" field in the cache to false, indicating no current errors.
     * - Clears the "errorReason" field in the cache, setting it to an empty string.
     * - Clears the "errorCode" field in the cache, setting it to an empty string.
     *
     * It ensures that any previously stored error-related data in the cache is removed,
     * allowing for a clean state for future computations or processes.
     */
    public void resetError() {
        CacheHandler.setValue("error", false);
        CacheHandler.setValue("errorReason", "");
        CacheHandler.setValue("errorCode", "");
    }

    /**
     * Resets the warning state in the system by clearing its associated values.
     * This method updates the cache to set the warning flag to false, clears the warning
     * reason, and resets the warning code to an empty string.
     *
     * The changes are applied using the CacheHandler to ensure the system reflects
     * the updated state, effectively indicating that no warnings are currently present.
     */
    public void resetWarning() {
        CacheHandler.setValue("warning", false);
        CacheHandler.setValue("warningReason", "");
        CacheHandler.setValue("warningCode", "");
    }

    /**
     * Toggles the mode between different states: "standby", a previously stored mode, or "dynamic".
     *
     * If the current mode is not "standby", it switches the mode to "standby" and*/
    public void toggleMode() {
        String currentIO = String.valueOf(CacheHandler.getValueOrDefault("mode", "standby"));
        String oldIO = String.valueOf(CacheHandler.getValueOrDefault("modeOld", ""));

        if (!currentIO.equals("standby")) {
            setMode("standby");
            CacheHandler.setValue("modeOld", currentIO);
        } else if (!oldIO.equals("")) {
            setMode(oldIO);
            CacheHandler.setValue("modeOld", "");
        } else {
            setMode("dynamic");
        }
    }

    /**
     * Sets the mode for the application or system by storing the given mode value in the cache.
     *
     * @param mode the mode to be set; this could represent*/
    public void setMode(String mode) {
        CacheHandler.setValue("mode", mode);

        log.info("Set Mode to {}.", mode);
    }

    /**
     * Resets the internal states of the object.
     * This method calls additional helper methods to perform
     * the necessary state reset actions, ensuring fail and warning
     * states are cleared.
     */
    public void resetStates() {
        resetFails();
        resetWarning();
    }
}
