package de.bytestore.pvheating.objects.config.provider;

import de.bytestore.pvheating.objects.Provider;
import lombok.Data;

import java.util.HashMap;

@Data
public class ProviderConfig {
    private HashMap<String, Provider> providers = new HashMap<String, Provider>();

    /**
     * Sets the provider configuration for the given name.
     *
     * @param nameIO            the name of the provider
     * @param configurationIO   the provider configuration
     */
    public void setProvider(String nameIO, Provider configurationIO) {
        if(providers.get(nameIO) == null) {
            this.providers.replace(nameIO, configurationIO);
        } else {
            this.providers.put(nameIO, configurationIO);
        }
    }

    /**
     * Retrieves the provider with the specified name.
     *
     * @param name the name of the provider
     * @return the provider associated with the name, or null if not found
     */
    public Provider getProvider(String name) {
        return providers.get(name);
    }
}
