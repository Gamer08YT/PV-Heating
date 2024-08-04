package de.bytestore.pvheating.objects;

import de.bytestore.pvheating.entity.GPIOType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Data
public class Provider {
    public Provider(String name, String provider) {
        this.name = name;
        this.provider = provider;
    }

    private String name;

    private String provider;

    private GPIOType[] types;
}
