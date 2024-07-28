package de.bytestore.pvheating.configuration.scr;

import de.bytestore.pvheating.jobs.SCRJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(Job.class)
public class SCRReloadConfiguration {
    private static final Logger log = LoggerFactory.getLogger(SCRReloadConfiguration.class);

    @Bean("stats_SCRReloadJob")
    JobDetail scrReloadJob() {
        return JobBuilder.newJob().ofType(SCRJob.class).storeDurably().withIdentity("scrReload").build();
    }

    @Bean("stats_SCRReloadTrigger")
    Trigger scrReloadTrigger(@Qualifier("stats_SCRReloadJob") JobDetail statsReloadJob) {
        // Print Debug Message.
        // Seconds | Minutes | Hours | Day of Month | Month | Day of Week | Year
        return TriggerBuilder.newTrigger().withIdentity("scrReloadTrigger").forJob(statsReloadJob).startNow().withSchedule(CronScheduleBuilder.cronSchedule("* * * * * ?")).build();
    }

}
