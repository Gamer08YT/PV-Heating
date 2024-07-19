package de.bytestore.pvheating.objects.config;

import de.bytestore.pvheating.entity.SCRType;
import lombok.Data;

@Data
public class SCRConfig {
    private SCRType type = SCRType.VOLTAGE;

    private double minVoltage = 0;

    private double maxVoltage = 10;

    private double minCurrent = 0;

    private double maxCurrent = 20;

    private double minPWM = 5000;

    private double maxPWM = 10000;

}
