package de.bytestore.pvheating.handler;

import com.pi4j.Pi4J;
import com.pi4j.boardinfo.definition.BoardModel;
import com.pi4j.context.Context;
import com.pi4j.event.InitializedListener;
import de.bytestore.pvheating.handler.devices.GPIOConfig;
import de.bytestore.pvheating.handler.devices.Raspberry;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class GPIOHandler {
    private static final Logger log = LoggerFactory.getLogger(GPIOHandler.class);

    @Getter
    private static ArrayList<GPIOConfig> configs = new ArrayList<GPIOConfig>();

    @Getter
    private static Context context;

    @Getter
    public static BoardModel modelIO;

    /**
     * Initializes the GPIOHandler.
     * - Creates a new Pi4J context using the newAutoContext() method.
     * - Adds InitializedListeners to the context to log initialization and shutdown events.
     * - Checks if the context is shutdown and logs the result.
     * - Gets the board model from the context.
     * - Logs the board model and label.
     * - Registers GPIO configurations using the registerConfigs() method.
     */
    public static void initialize() {
        context = Pi4J.newAutoContext();

        context.addListener((InitializedListener) initializedEvent -> {
            log.info("Successfully initialized GPIOHandler.");
        });

        context.addListener((InitializedListener) initializedEvent -> {
            log.info("Successfully shutdowned GPIOHandler.");
        });

        log.info("Shutdown? {}." , context.isShutdown());

        // Get Board Model.
        modelIO = context.boardInfo().getBoardModel();

        // Print some "Debug" Info.
        log.info("Running on Board Model '{}'.", modelIO.getLabel());

        registerConfigs();
    }

    /**
     * Register the default GPIO configurations.
     */
    private static void registerConfigs() {
        configs.add(new Raspberry());
    }
}
