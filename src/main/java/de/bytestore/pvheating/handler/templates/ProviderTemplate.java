package de.bytestore.pvheating.handler.templates;

import de.bytestore.pvheating.entity.GPIOChannelType;
import de.bytestore.pvheating.entity.GPIOType;
import de.bytestore.pvheating.handler.interfaces.ProviderTemplateInterface;
import de.bytestore.pvheating.objects.config.provider.ProviderConfig;
import lombok.Getter;

public class ProviderTemplate implements ProviderTemplateInterface {
    @Getter
    private Object cachedValue = null;


    @Override
    public String name() {
        return "";
    }

    @Override
    public GPIOType[] type() {
        return new GPIOType[0];
    }


    @Override
    public GPIOChannelType channelType() {
        return null;
    }

    @Override
    public void onInput(Object valueIO) {

    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public String suffix() {
        return null;
    }

    /**
     * Sets the value of the cachedValue variable and triggers the onInput method.
     *
     * @param valueIO the value to be set
     */
    protected void setValue(Object valueIO) {
        this.cachedValue = valueIO;

        onInput(valueIO);
    }


    /**
     * This method retrieves the provider associated with the current instance.
     *
     * @return the provider associated with the current instance*/
    protected String getProvider() {
        return ProviderConfig.getProvider(this.name()).getProvider();
    }
}
