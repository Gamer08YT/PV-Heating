package de.bytestore.pvheating.jobs;

import de.bytestore.pvheating.handler.CacheHandler;
import de.bytestore.pvheating.service.GPIOService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.numbers.core.Precision;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.DecimalFormat;

@Slf4j
public class FlowJob implements Job {
    // 7.5 Steps = 1 L/min
    private static final double flowFactor = 7.5;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        var flowVolume = GPIOService.getFLOW_PULSE_COUNT() / flowFactor;

        // Flow per Second.
        CacheHandler.setValue("flow-per-second", Precision.round(flowVolume / 60, 0));

        // Round to 2 decimal places.
        flowVolume = Precision.round(flowVolume, 2);

        // Flow per Minute.
        CacheHandler.setValue("flow-per-minute", flowVolume);

        // Reset Flow Counter.
        GPIOService.setFLOW_PULSE_COUNT(0);

        log.debug("Reset Flow Counter. Flow Volume: {} L/min.", flowVolume);
    }
}
