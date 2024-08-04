package de.bytestore.pvheating.view.bindings;


import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.entity.GPIOChannelType;
import de.bytestore.pvheating.entity.GPIOType;
import de.bytestore.pvheating.handler.ConfigHandler;
import de.bytestore.pvheating.handler.GPIOHandler;
import de.bytestore.pvheating.handler.templates.ProviderTemplate;
import de.bytestore.pvheating.objects.Provider;
import de.bytestore.pvheating.service.ModbusService;
import de.bytestore.pvheating.service.Pi4JService;
import de.bytestore.pvheating.service.ProviderBeanService;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.flowui.component.combobox.JmixComboBox;
import io.jmix.flowui.component.virtuallist.JmixVirtualList;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Route(value = "bindings-view", layout = MainView.class)
@ViewController("heater_BindingsView")
@ViewDescriptor("bindings-view.xml")
public class BindingsView extends StandardView {
    @Autowired
    private ModbusService modbusService;

    @Autowired
    private Pi4JService pi4JService;

    @ViewComponent
    private JmixVirtualList<Provider> providerItems;

    @ViewComponent
    private JmixComboBox<Provider> providerValue;

    @ViewComponent
    private JmixComboBox<ProviderTemplate> providerSelector;

    @Autowired
    private ProviderBeanService providerBeanService;

    @Subscribe
    public void onInit(final InitEvent event) {
        //System.out.println(providerService.getChildren());

        //providerItems.setItems();
        providerSelector.setItems(providerBeanService.getChildren());
        providerValue.setItems(getProviders());

        this.setProviderConfig();
    }

    /**
     * Sets the provider configuration by reading the provider configuration file and updating the UI components.
     */
    private void setProviderConfig() {
        //ConfigHandler.readProvider();

        //providerItems.setItems(ConfigHandler.getProviderConfig().getProviders().values());
    }

    @Subscribe("providerSelector")
    public void onProviderSelectorComponentValueChange(final AbstractField.ComponentValueChangeEvent<JmixComboBox<?>, ?> event) {
        setValueSelector(providerSelector.getValue());
    }

    /**
     * Sets the value selector based on the given provider template.
     *
     * @param valueIO The provider template used to filter the list of providers.
     */
    private void setValueSelector(ProviderTemplate valueIO) {
        ArrayList<Provider> valueProvider = getProviders();
        ArrayList<Provider> newItems = new ArrayList<>();

        Set<GPIOType> set = new HashSet<>(Arrays.asList(valueIO.type()));

        valueProvider.forEach(provider -> {
            boolean anyMatch = Arrays.stream(provider.getTypes())
                    .anyMatch(set::contains);

            if(anyMatch) {
                newItems.add(provider);
            }
        });

        providerValue.setItems(newItems);
    }


    @Supply(to = "providerSelector", subject = "renderer")
    private Renderer<ProviderTemplate> providerSelectorRenderer() {
        return new ComponentRenderer<>(object -> {
            HorizontalLayout layoutIO = new HorizontalLayout();
            layoutIO.setPadding(true);
            layoutIO.addClassName("card");

            layoutIO.add(new Span(object.name()));

            // Add GPIO Type.
            List.of(object.type()).forEach(gpioType -> {
                Span badgeIO = new Span(gpioType.name());
                badgeIO.getElement().getThemeList().add("badge");
                badgeIO.getElement().getThemeList().add("success");

                layoutIO.add(badgeIO);
            });


            Span badgeIO = new Span(object.channelType().name());
            badgeIO.getElement().getThemeList().add("badge");
            badgeIO.getElement().getThemeList().add((object.channelType().equals(GPIOChannelType.INPUT) ? "normal" : "contrast"));

            layoutIO.add(badgeIO);

            return layoutIO;
        });
    }

    @Supply(to = "providerValue", subject = "renderer")
    private Renderer<Provider> providerItemsRenderer() {
        return new ComponentRenderer<>(object -> {
            HorizontalLayout layoutIO = new HorizontalLayout();
            layoutIO.setPadding(true);
            layoutIO.addClassName("card");

            layoutIO.add(new Span(object.getName()));

            Arrays.stream(object.getTypes()).forEach(gpioType -> {
                Span badgeIO = new Span(gpioType.name());
                badgeIO.getElement().getThemeList().add("badge");
                badgeIO.getElement().getThemeList().add("success");

                layoutIO.add(badgeIO);
            });

            Span badgeIO = new Span(object.getProvider());
            badgeIO.getElement().getThemeList().add("badge");
            badgeIO.getElement().getThemeList().add("normal");

            layoutIO.add(badgeIO);

            return layoutIO;
        });
    }


    /**
     * Retrieves the list of providers.
     *
     * @return The list of providers.
     */
    private ArrayList<Provider> getProviders() {
        ArrayList<Provider> providersIO = new ArrayList<Provider>();

        // Add GPIO provider.
        GPIOHandler.getConfigs().get(0).getPins().forEach(gpioItem -> {
            providersIO.add(new Provider(gpioItem.getName(), "gpio", gpioItem.getType().toArray(new GPIOType[0])));
        });

        // Add 1 Wire provider.
        if(pi4JService.getWire1fails() < 3) {
            pi4JService.get1WireDevices().forEach(deviceItem -> {
                providersIO.add(new Provider(deviceItem, "wire1", new GPIOType[]{
                        GPIOType.WIRE1
                }));
            });
        }

        modbusService.getConfig().getPower().getModbus().getSensors().forEach((keyIO, addressIO) -> {
            providersIO.add(new Provider(keyIO, "modbus", new GPIOType[]{
                    GPIOType.MODBUS
            }));
        });

        return providersIO;
    }

    @Subscribe(id = "providerSave", subject = "clickListener")
    public void onProviderSaveClick(final ClickEvent<JmixButton> event) {
        ConfigHandler.getProviderConfig().setProvider(providerSelector.getValue().name(), providerValue.getValue());
        ConfigHandler.saveProvider();
    }

}