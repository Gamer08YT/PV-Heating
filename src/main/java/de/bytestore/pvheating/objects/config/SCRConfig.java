package de.bytestore.pvheating.objects.config;

import de.bytestore.pvheating.entity.SCRType;
import lombok.Data;

@Data
public class SCRConfig {
    private SCRType type = SCRType.VOLTAGE;

    private long minVoltage = 0;

    private long maxVoltage = 10;

    private long minCurrent = 0;

    private long maxCurrent = 20;

    private long minPWM = 5000;

    private long maxPWM = 10000;

}
