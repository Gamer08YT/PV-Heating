package de.bytestore.pvheating.handler.devices;

import de.bytestore.pvheating.objects.gpio.GPIOItem;
import lombok.Data;

import java.util.ArrayList;

@Data
public class GPIOConfig {
    public ArrayList<GPIOItem> items = new ArrayList<GPIOItem>();
}
