package de.bytestore.pvheating.configuration.temperature;

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
public class TemperatureConfiguration {
    @Bean("temperatureExecutor")
    public TaskExecutor temperatureExecutor() {
        ThreadPoolTaskExecutor poolIO = new ThreadPoolTaskExecutor();

        poolIO.setCorePoolSize(1);
        poolIO.setMaxPoolSize(1);
        poolIO.setQueueCapacity(1);

        return poolIO;
    }
}
