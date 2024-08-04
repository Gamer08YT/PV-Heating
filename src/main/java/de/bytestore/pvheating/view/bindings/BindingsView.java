package de.bytestore.pvheating.view.bindings;


import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.handler.GPIOHandler;
import de.bytestore.pvheating.objects.Provider;
import de.bytestore.pvheating.service.ModbusService;
import de.bytestore.pvheating.service.Pi4JService;
import de.bytestore.pvheating.service.ProviderBeanService;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.flowui.component.combobox.JmixComboBox;
import io.jmix.flowui.component.virtuallist.JmixVirtualList;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

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
    private JmixComboBox<String> providerSelector;

    @Autowired
    private ProviderBeanService providerBeanService;

    @Subscribe
    public void onInit(final InitEvent event) {
        //System.out.println(providerService.getChildren());

        //providerItems.setItems();
        providerBeanService.test();
        //providerService.test();
        //providerSelector.setItems(providerService.getChildren().stream().map(gpioListener -> gpioListener.name() != null ? gpioListener.name() : "Unknown").toArray(String[]::new));
        providerValue.setItems(getProviders());
    }

    @Supply(to = "providerValue", subject = "renderer")
    private Renderer<Provider> providerItemsRenderer() {
        return new ComponentRenderer<>(object -> {
            HorizontalLayout layoutIO = new HorizontalLayout();
            layoutIO.setPadding(true);
            layoutIO.addClassName("card");

            layoutIO.add(new Span(object.getName()));

            Span badgeIO = new Span(object.getProvider());
            badgeIO.getElement().getThemeList().add("badge");
            badgeIO.getElement().getThemeList().add("success");

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
            providersIO.add(new Provider(gpioItem.getName(), "gpio"));
        });

        // Add 1 Wire provider.
        if(pi4JService.getWire1fails() < 3) {
            pi4JService.get1WireDevices().forEach(deviceItem -> {
                providersIO.add(new Provider(deviceItem, "wire1"));
            });
        }

        modbusService.getConfig().getPower().getModbus().getSensors().forEach((keyIO, addressIO) -> {
            providersIO.add(new Provider(keyIO, "modbus"));
        });

        return providersIO;
    }

}