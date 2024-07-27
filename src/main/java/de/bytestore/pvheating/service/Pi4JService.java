package de.bytestore.pvheating.service;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmType;
import com.pi4j.library.pigpio.PiGpio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class Pi4JService {
    private final Context pi4jContext;

    // Store already registered GPIO pins.
    private static HashMap<Integer, Object> provider = new HashMap<Integer, Object>();

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

        return digitalInput.state().isHigh();
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
}
