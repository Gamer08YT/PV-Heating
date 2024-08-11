package de.bytestore.pvheating.configuration.homeassistant;

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
public class HomeAssistantConfiguration {
    @Bean("homeAssistantExecutor")
    public TaskExecutor homeAssistantExecutor() {
        ThreadPoolTaskExecutor poolIO = new ThreadPoolTaskExecutor();

        poolIO.setCorePoolSize(1);
        poolIO.setMaxPoolSize(2);
        poolIO.setQueueCapacity(10);

        return poolIO;
    }
}
