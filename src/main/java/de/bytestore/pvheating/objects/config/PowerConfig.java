package de.bytestore.pvheating.objects.config;

import lombok.Data;

@Data
public class PowerConfig {
    private double minPower = 100;

    private double maxPower = 3000;
}
