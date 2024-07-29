package de.bytestore.pvheating.objects.config.system;

import lombok.Data;

import java.util.Map;

@Data
public class ModbusConfig {
    private boolean enabled = true;

    private String port = "/dev/ttyUSB0";

    private int baud = 9600;

    private int dataBits = 8;

    private int stopBits = 1;

    private int parity = 0;


    private Map<String, Integer> sensors = Map.of(
            "total-work", 72,
            "current-power", 52
    );
}
