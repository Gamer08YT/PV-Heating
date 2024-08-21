package de.bytestore.pvheating.objects.config.provider;

import de.bytestore.pvheating.handler.ConfigHandler;
import de.bytestore.pvheating.objects.Provider;
import lombok.Data;

import java.util.ArrayList;

@Data
public class ProviderConfig {
    private ArrayList<Provider> providers = new ArrayList<>();

    /**
     * Retrieves the provider with the specified name.
     *
     * @param nameIO the name of the provider
     * @return the provider associated with the name, or null if not found
     */
    public static Provider getProvider(String nameIO) {
        for (Provider provider : ConfigHandler.getProviderConfig().getProviders()) {
            if (provider != null && provider.getType().equals(nameIO))
                return provider;
        }

        return null;
    }

    /**
     * Sets the provider configuration by adding or updating a provider with the specified name and configuration.
     * If a provider with the same name already exists, it will be updated with the new configuration. If not, a new provider will be added.
     *
     * @param nameIO          the name of the provider
     * @param configurationIO the configuration of the provider
     * @param name
     */
    public void setProvider(String nameIO, Provider configurationIO, String name) {
        Provider providerIO = getProvider(name);

        if (providerIO != null)
            providers.remove(providerIO);

        configurationIO.setType(name);

        providers.add(configurationIO);
    }


}
