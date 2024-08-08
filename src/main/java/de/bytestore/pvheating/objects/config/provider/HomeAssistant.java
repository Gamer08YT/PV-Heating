package de.bytestore.pvheating.objects.config.provider;

import lombok.Data;

@Data
public class HomeAssistant {
    private String url = "http://homeassistant.local:8123/api/states/";

    private String token = "";

    private boolean publishStates = true;
}
