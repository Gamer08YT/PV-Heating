package de.bytestore.pvheating.configuration.button;

import de.bytestore.pvheating.jobs.ButtonJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(Job.class)
public class ButtonReloadConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ButtonReloadConfiguration.class);

    @Bean("stats_ButtonReloadJob")
    JobDetail buttonReloadJob() {
        return JobBuilder.newJob().ofType(ButtonJob.class).storeDurably().withIdentity("buttonReload").build();
    }

    @Bean("stats_buttonReloadTrigger")
    Trigger buttonReloadTrigger(@Qualifier("stats_ButtonReloadJob") JobDetail statsReloadJob) {
        // Print Debug Message.
        // Seconds | Minutes | Hours | Day of Month | Month | Day of Week | Year
        return TriggerBuilder.newTrigger().withIdentity("buttonReloadTrigger").forJob(statsReloadJob).startNow().withSchedule(CronScheduleBuilder.cronSchedule("* * * * * ?")).build();
    }

}
