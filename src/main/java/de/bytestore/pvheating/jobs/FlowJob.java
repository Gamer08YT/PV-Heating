package de.bytestore.pvheating.jobs;

import de.bytestore.pvheating.handler.CacheHandler;
import de.bytestore.pvheating.service.GPIOService;
import de.bytestore.pvheating.service.Pi4JService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.numbers.core.Precision;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class FlowJob implements Job {
    // 7.5 Steps = 1 L/min
    private static final double flowFactor = 7.5;

    private static int flowCounterError = 0;

    @Autowired
    private Pi4JService pi4j;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // Count Flow Steps.
        handleFlow();

        // Check if Pump is stuck.
        checkPump();
    }

    private void checkPump() {
        if (pi4j.isPumpEnabled()) {
            // Check if flow is higher than 0.5 L/min.
            if (((double) CacheHandler.getValueOrDefault("flow-per-minute", 0.00)) < 0.5) {
                flowCounterError++;

                if (flowFactor > 10) {
                    pi4j.triggerError("Circulation loop interrupted.", 10);
                }
            } else {
                flowCounterError = 0;
            }
        }
    }

    /**
     * Handles the*/
    private void handleFlow() {
        var flowVolume = GPIOService.getFLOW_PULSE_COUNT() / flowFactor;

//        // Flow per Second.
//        CacheHandler.setValue("flow-per-second", Precision.round(flowVolume / 60, 0));

        // Round to 2 decimal places.
        flowVolume = Precision.round(flowVolume, 2);

        // Flow per Minute.
        CacheHandler.setValue("flow-per-minute", flowVolume);

        // Reset Flow Counter.
        GPIOService.setFLOW_PULSE_COUNT(0);

        log.debug("Reset Flow Counter. Flow Volume: {} L/min.", flowVolume);
    }
}
