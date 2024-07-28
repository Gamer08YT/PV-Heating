package de.bytestore.pvheating.service;

import com.pi4j.boardinfo.definition.BoardModel;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.pi4j.io.gpio.digital.DigitalState.HIGH;

@Service
public class Pi4JService {
    private static final Logger log = LoggerFactory.getLogger(Pi4JService.class);
    private final Context pi4jContext;

    // Store already registered GPIO pins.
    private static HashMap<Integer, Object> provider = new HashMap<Integer, Object>();
    private static final String W1_DEVICE_FOLDER = "/sys/bus/w1/devices/";

    @Autowired
    public Pi4JService(@Autowired Context pi4jContext) {
        this.pi4jContext = pi4jContext;
    }

    /**
     * Sets the state of a pin based on the given pin number and state.
     *
     * @param pinIO The pin number to set the state for.
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
     *         Returns -1 if there was an error reading the temperature.
     */
    public double readDS18B20(String deviceIO) {
        List<String> lines = get1Wire(deviceIO, "w1_slave");

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

        return -1;
    }

    /**
     * Retrieves the contents of a specified 1-Wire file.
     *
     * @param deviceIO The name of the 1-Wire device folder.
     * @param dirIO The name of the 1-Wire file within the device folder.
     * @return A list of strings representing the contents of the specified 1-Wire file.
     * @throws RuntimeException if an I/O error occurs while reading the file.
     */
    public List<String> get1Wire(String deviceIO, String dirIO) {
        try {
            return Files.readAllLines(new File(W1_DEVICE_FOLDER + deviceIO + "/" + dirIO).toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public void setPWM(int pinIO, Double value) {
        System.out.println("TEST:");
        System.out.println(Pi4JService.provider);

        Pwm pwmConfig = null;

        if (!Pi4JService.provider.containsKey(pinIO)) {
            pwmConfig = pi4jContext.create(Pwm.newConfigBuilder(pi4jContext).pwmType(PwmType.SOFTWARE).initial(0).shutdown(0).address(pinIO).build()) ;

            // Put Provider to Cache.
            Pi4JService.provider.put(pinIO, pwmConfig);
        } else
            pwmConfig = (Pwm) Pi4JService.provider.get(pinIO);

        if(value > 0)
            pwmConfig.on(value);
        else
            pwmConfig.off();
    }


    public Context getPi4jContext() {
        return pi4jContext;
    }

    public BoardModel getModel() {
        return pi4jContext.boardInfo().getBoardModel();
    }
}
