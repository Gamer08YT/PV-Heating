package de.bytestore.pvheating.service;

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
//        while (true) {
//            try {
//                System.out.println(service.getPinState(4));
//
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }

        service.getPi4jContext().getDigitalInputProvider().create(22).addListener(event -> {
            System.out.println("PIN CHANGED " + event.state().toString());

            if(event.state().isHigh()) {
                // Increment Pulses.
                GPIOService.FLOW_PULSE_COUNT++;
            }
        });

        log.info("Registered GPIO Listener.");
    }
}
