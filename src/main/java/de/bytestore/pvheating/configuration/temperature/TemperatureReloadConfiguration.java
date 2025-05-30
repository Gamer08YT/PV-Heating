package de.bytestore.pvheating.configuration.temperature;

import de.bytestore.pvheating.jobs.TemperatureJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(Job.class)
public class TemperatureReloadConfiguration {
    private static final Logger log = LoggerFactory.getLogger(TemperatureReloadConfiguration.class);

    @Bean("stats_TemperatureReloadJob")
    JobDetail temperatureReloadJob() {
        return JobBuilder.newJob().ofType(TemperatureJob.class).storeDurably().withIdentity("temperatureReload").build();
    }

    @Bean("stats_temperatureReloadTrigger")
    Trigger temperatureReloadTrigger(@Qualifier("stats_TemperatureReloadJob") JobDetail statsReloadJob) {
        // Print Debug Message.
        // Seconds | Minutes | Hours | Day of Month | Month | Day of Week | Year
        return TriggerBuilder.newTrigger().withIdentity("temperatureReloadTrigger").forJob(statsReloadJob).startNow().withSchedule(CronScheduleBuilder.cronSchedule("* * * * * ?")).build();
    }

}
