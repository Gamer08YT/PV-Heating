package de.bytestore.pvheating.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.bytestore.pvheating.objects.config.SystemConfig;
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
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Getter
    private static SystemConfig cached = null;

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
        try {
            cached = gson.fromJson(FileUtils.readFileToString(config, Charset.defaultCharset()), SystemConfig.class);
        } catch (IOException e) {
            log.error("Unable to read Config from disk.", e);
        }
    }
}
