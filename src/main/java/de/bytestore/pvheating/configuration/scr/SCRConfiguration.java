package de.bytestore.pvheating.configuration.scr;

import io.jmix.core.annotation.JmixModule;
import io.jmix.data.DataConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@ComponentScan
@ConfigurationPropertiesScan
@JmixModule(dependsOn = DataConfiguration.class)
public class SCRConfiguration {
    @Bean("scrExecutor")
    public TaskExecutor scrExecutor() {
        ThreadPoolTaskExecutor poolIO = new ThreadPoolTaskExecutor();

        poolIO.setCorePoolSize(1);
        poolIO.setMaxPoolSize(1);
        poolIO.setQueueCapacity(1);

        return poolIO;
    }
}
