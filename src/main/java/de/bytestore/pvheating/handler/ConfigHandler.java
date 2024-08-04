package de.bytestore.pvheating.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.bytestore.pvheating.objects.config.gpio.GPIOConfig;
import de.bytestore.pvheating.objects.config.provider.ProviderConfig;
import de.bytestore.pvheating.objects.config.system.SystemConfig;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class ConfigHandler {
    private static final Logger log = LoggerFactory.getLogger(ConfigHandler.class);

    @Getter
    public static File config = new File("./config.json");

    @Getter
    public static File provider = new File("./provider.json");

    @Getter
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Getter
    private static SystemConfig cached = null;

    @Getter
    private static ProviderConfig providerConfig = null;

    /**
     * Checks if the config file exists and creates it if it doesn't.
     * This method reads a default config and writes it to disk if the config file doesn't exist.
     * The default config is a JSON representation of a SystemConfig object.
     * If there is an error while writing the config to disk, an error message is logged.
     */
    public static void createIfNotExists() {
        if (!config.exists()) {
            String dataIO = getDefaultConfig();

            try {
                // Write Config to Disk.
                FileUtils.writeStringToFile(config, dataIO, Charset.defaultCharset());
            } catch (IOException e) {
                log.error("Unable to write Default Config to Disk.", e);
            }
        }

        if (!provider.exists()) {
            String dataIO = getDefaultGPIOConfig();

            try {
                // Write Config to Disk.
                FileUtils.writeStringToFile(provider, dataIO, Charset.defaultCharset());
            } catch (IOException e) {
                log.error("Unable to write Default Provider Config to Disk.", e);
            }
        }
    }

    /**
     * Retrieves the default configuration for GPIO as a JSON string representation of a GPIOConfig object.
     *
     * @return the default configuration for GPIO as a JSON string
     */
    private static String getDefaultGPIOConfig() {
        return gson.toJson(new GPIOConfig());
    }

    /**
     * Retrieves the default configuration as a JSON string representation of a SystemConfig object.
     *
     * @return the default configuration as a JSON string
     */
    private static String getDefaultConfig() {
        return gson.toJson(new SystemConfig());
    }

    /**
     * Reads the configuration file and parses it into a SystemConfig object.
     * If the configuration file does not exist, it creates a default configuration
     * and writes it to disk.
     * <p>
     * The configuration file is expected to be a JSON file located at the
     * path specified by the 'config' field in the ConfigHandler class.
     */
    public static void readConfig() {
        log.info("Reading System configuration file.");

        try {
            cached = gson.fromJson(FileUtils.readFileToString(config, Charset.defaultCharset()), SystemConfig.class);
        } catch (IOException e) {
            log.error("Unable to read System Config from disk.", e);
        }
    }

    /**
     * Reads the GPIO configuration file and parses it into a GPIOConfig object.
     * If the configuration file does not exist, it throws an IOException.
     * <p>
     * The configuration file is expected to be a JSON file located at the path specified by the 'config' field in the ConfigHandler class.
     */
    public static void readProvider() {
        log.info("Reading Provider configuration file.");

        try {
            providerConfig = gson.fromJson(FileUtils.readFileToString(provider, Charset.defaultCharset()), ProviderConfig.class);
        } catch (IOException e) {
            log.error("Unable to read Provider Config from disk.", e);
        }
    }

    /**
     * Saves the configuration to disk.
     * This method converts the SystemConfig object to a JSON string using Gson library,
     * and writes it to the 'config' file specified in the ConfigHandler class.
     * If an error occurs while saving the configuration, an error message is logged.
     */
    public static void saveConfig() {
        try {
            FileUtils.writeStringToFile(config, gson.toJson(cached), Charset.defaultCharset());
        } catch (IOException e) {
            log.error("Unable to save Config to disk.", e);
        }
    }

    /**
     * Saves the GPIO configuration to disk.
     *
     * This method converts the GPIOConfig object to a JSON string using the Gson library,
     * and writes it to the file specified in the 'gpio' field of the ConfigHandler class.
     * If an error occurs while saving the configuration, an error message is logged.
     */
    public static void saveProvider() {
        try {
            FileUtils.writeStringToFile(provider, gson.toJson(providerConfig), Charset.defaultCharset());
        } catch (IOException e) {
            log.error("Unable to save GPIO Config to disk.", e);
        }
    }
}
