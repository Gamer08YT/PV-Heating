package de.bytestore.pvheating.handler.templates.status;

import org.springframework.stereotype.Component;

@Component
public class SCRErrorTemplate extends ButtonResetTemplate {
    @Override
    public String name() {
        return "scr-error";
    }


}
