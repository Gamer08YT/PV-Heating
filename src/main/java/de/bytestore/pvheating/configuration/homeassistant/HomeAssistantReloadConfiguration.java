package de.bytestore.pvheating.configuration.homeassistant;

import de.bytestore.pvheating.jobs.HomeAssistantJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(Job.class)
public class HomeAssistantReloadConfiguration {
    private static final Logger log = LoggerFactory.getLogger(HomeAssistantReloadConfiguration.class);

    @Bean("stats_HomeAssistantReloadJob")
    JobDetail homeAssistantReloadJob() {
        return JobBuilder.newJob().ofType(HomeAssistantJob.class).storeDurably().withIdentity("homeAssistantReload").build();
    }

    @Bean("stats_homeAssistantReloadTrigger")
    Trigger homeAssistantReloadTrigger(@Qualifier("stats_HomeAssistantReloadJob") JobDetail statsReloadJob) {
        // Print Debug Message.
        // Seconds | Minutes | Hours | Day of Month | Month | Day of Week | Year
        return TriggerBuilder.newTrigger().withIdentity("homeAssistantReloadTrigger").forJob(statsReloadJob).startNow().withSchedule(CronScheduleBuilder.cronSchedule("* * * * * ?")).build();
    }

}
