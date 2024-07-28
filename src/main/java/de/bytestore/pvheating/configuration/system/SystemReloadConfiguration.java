package de.bytestore.pvheating.configuration.system;

import de.bytestore.pvheating.jobs.SystemJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(Job.class)
public class SystemReloadConfiguration {
    private static final Logger log = LoggerFactory.getLogger(SystemReloadConfiguration.class);

    @Bean("stats_SystemReloadJob")
    JobDetail systemReloadJob() {
        return JobBuilder.newJob().ofType(SystemJob.class).storeDurably().withIdentity("systemReload").build();
    }

    @Bean("stats_SystemReloadTrigger")
    Trigger systemReloadTrigger(@Qualifier("stats_SystemReloadJob") JobDetail statsReloadJob) {
        // Print Debug Message.
        // Seconds | Minutes | Hours | Day of Month | Month | Day of Week | Year
        return TriggerBuilder.newTrigger().withIdentity("systemReloadTrigger").forJob(statsReloadJob).startNow().withSchedule(CronScheduleBuilder.cronSchedule("* * * * * ?")).build();
    }

}
