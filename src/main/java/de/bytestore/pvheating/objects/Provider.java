package de.bytestore.pvheating.objects;

import de.bytestore.pvheating.entity.GPIOType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class Provider {
    public Provider(String name, String provider) {
        this.name = name;
        this.provider = provider;
    }

    public Provider(String name, String provider, String type) {
        this.name = name;
        this.provider = provider;
        this.type = type;
    }

    public Provider(String name, String provider, GPIOType[] types) {
        this.name = name;
        this.provider = provider;
        this.types = types;
    }

    // For eq. Pump Output
    private String name;

    // For example Modbus
    private String provider;

    // For Example Pump.
    private String type;

    private GPIOType[] types;
}
