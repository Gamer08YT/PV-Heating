package de.bytestore.pvheating.configuration.modbus;

import de.bytestore.pvheating.jobs.ModbusJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(Job.class)
public class ModbusReloadConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ModbusReloadConfiguration.class);

    @Bean("stats_ModbusReloadJob")
    JobDetail modbusReloadJob() {
        return JobBuilder.newJob().ofType(ModbusJob.class).storeDurably().withIdentity("modbusReload").build();
    }

    @Bean("stats_modbusReloadTrigger")
    Trigger modbusReloadTrigger(@Qualifier("stats_ModbusReloadJob") JobDetail statsReloadJob) {
        // Print Debug Message.
        // Seconds | Minutes | Hours | Day of Month | Month | Day of Week | Year
        return TriggerBuilder.newTrigger().withIdentity("modbusReloadTrigger").forJob(statsReloadJob).startNow().withSchedule(CronScheduleBuilder.cronSchedule("* * * * * ?")).build();
    }

}
