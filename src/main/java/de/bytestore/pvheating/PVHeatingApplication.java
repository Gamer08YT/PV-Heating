package de.bytestore.pvheating;

import com.google.common.base.Strings;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import de.bytestore.pvheating.handler.ConfigHandler;
import de.bytestore.pvheating.handler.GPIOHandler;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Push
@Theme(value = "PV-Heating")
@PWA(name = "PV Heating", shortName = "PV Heating", description = "A simple application to control a PV heating system, Developed by Jan Heil (www.byte-store.de).", offline = true)
@SpringBootApplication
public class PVHeatingApplication implements AppShellConfigurator {

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        // PIGPIO which remote Raspberry Pi to connect to
        System.setProperty("pi4j.host", "192.168.3.193");
        System.setProperty("pipgio.host", "192.168.3.193");
        System.setProperty("pipgio.remote", "true");

        SpringApplication.run(PVHeatingApplication.class, args);
    }

    @EventListener
    public void onApplicationStarted(final ApplicationStartedEvent event) {
        ConfigHandler.createIfNotExists();
        ConfigHandler.readConfig();
        ConfigHandler.readProvider();
    }

    @Bean
    @Primary
    @ConfigurationProperties("main.datasource")
    DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("main.datasource.hikari")
    DataSource dataSource(final DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @EventListener
    public void printApplicationUrl(final ApplicationStartedEvent event) {
        LoggerFactory.getLogger(PVHeatingApplication.class).info("Application started at " + "http://localhost:" + environment.getProperty("local.server.port") + Strings.nullToEmpty(environment.getProperty("server.servlet.context-path")));
    }
}
