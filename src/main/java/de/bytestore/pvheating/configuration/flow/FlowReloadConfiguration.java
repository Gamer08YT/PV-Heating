package de.bytestore.pvheating.configuration.flow;

import de.bytestore.pvheating.jobs.FlowJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(Job.class)
public class FlowReloadConfiguration {
    private static final Logger log = LoggerFactory.getLogger(FlowReloadConfiguration.class);

    @Bean("stats_FlowReloadJob")
    JobDetail flowReloadJob() {
        return JobBuilder.newJob().ofType(FlowJob.class).storeDurably().withIdentity("flowReload").build();
    }

    @Bean("stats_flowReloadTrigger")
    Trigger flowReloadTrigger(@Qualifier("stats_FlowReloadJob") JobDetail statsReloadJob) {
        // Print Debug Message.
        // Seconds | Minutes | Hours | Day of Month | Month | Day of Week | Year
        return TriggerBuilder.newTrigger().withIdentity("flowReloadTrigger").forJob(statsReloadJob).startNow().withSchedule(CronScheduleBuilder.cronSchedule("* * * * * ?")).build();
    }

}
