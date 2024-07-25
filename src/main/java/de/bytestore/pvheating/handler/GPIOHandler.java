package de.bytestore.pvheating.handler;

import com.pi4j.Pi4J;
import com.pi4j.boardinfo.definition.BoardModel;
import com.pi4j.context.Context;
import com.pi4j.event.InitializedListener;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GPIOHandler {
    private static final Logger log = LoggerFactory.getLogger(GPIOHandler.class);

    @Getter
    private static Context context;

    @Getter
    public static BoardModel modelIO;

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
    }
}
