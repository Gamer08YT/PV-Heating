package de.bytestore.pvheating.objects.config.system;

import lombok.Data;

@Data
public class PowerConfig {
    private double minPower = 100;

    private double maxPower = 3000;

    private double offsetPower = 300;
}
