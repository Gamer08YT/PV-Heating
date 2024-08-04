package de.bytestore.pvheating.configuration.stats;

import de.bytestore.pvheating.jobs.ModbusJob;
import de.bytestore.pvheating.jobs.StatsJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(Job.class)
public class StatsReloadConfiguration {
    private static final Logger log = LoggerFactory.getLogger(StatsReloadConfiguration.class);

    @Bean("stats_StatsReloadJob")
    JobDetail statsReloadJob() {
        return JobBuilder.newJob().ofType(StatsJob.class).storeDurably().withIdentity("statsReload").build();
    }

    @Bean("stats_statsReloadTrigger")
    Trigger statsReloadTrigger(@Qualifier("stats_StatsReloadJob") JobDetail statsReloadJob) {
        // Print Debug Message.
        // Seconds | Minutes | Hours | Day of Month | Month | Day of Week | Year
        return TriggerBuilder.newTrigger().withIdentity("statsReloadTrigger").forJob(statsReloadJob).startNow().withSchedule(CronScheduleBuilder.cronSchedule("* * * * * ?")).build();
    }

}
