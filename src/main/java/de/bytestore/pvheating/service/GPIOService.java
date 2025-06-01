package de.bytestore.pvheating.service;

import com.pi4j.io.binding.DigitalBinding;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.PullResistance;
import de.bytestore.pvheating.configuration.DefaultPinout;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GPIOService {
    @Getter
    @Setter
    private static int FLOW_PULSE_COUNT = 0;

    @Autowired
    public GPIOService(@Autowired Pi4JService service) {
        /**
         * Handle Flow Meter.
         */
        var builderIO = DigitalInputConfig.newBuilder(service.getPi4jContext()).address(DefaultPinout.FLOW_METER_GPIO).debounce(500L).name("FlowMeter").pull(PullResistance.PULL_UP).build();

        service.getPi4jContext().create(builderIO).addListener(event -> {
            if (event.state().isHigh()) {
                // Increment Pulses.
                GPIOService.FLOW_PULSE_COUNT++;
            }
        });

        /**
         * Handle Status Button.
         */
        var statusBtnBuilder = DigitalInputConfig.newBuilder(service.getPi4jContext()).address(DefaultPinout.STATUS_BUTTON_GPIO).debounce(500L).name("StatusButton").pull(PullResistance.PULL_UP).build();

        service.getPi4jContext().create(statusBtnBuilder).addListener(event -> {
            service.toggleMode();
        });

        /**
         * Handle Fault Button.
         */
        var faultBtnBuilder = DigitalInputConfig.newBuilder(service.getPi4jContext()).address(DefaultPinout.FAULT_BUTTON_GPIO).debounce(500L).name("FaultButton").pull(PullResistance.PULL_UP).build();


        service.getPi4jContext().create(faultBtnBuilder).addListener(event -> {
            service.resetStates();
        });


        log.info("Registered GPIO Listener.");
    }
}
