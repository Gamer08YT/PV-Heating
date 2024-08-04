package de.bytestore.pvheating.view.bindings;


import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.handler.ConfigHandler;
import de.bytestore.pvheating.handler.GPIOHandler;
import de.bytestore.pvheating.objects.Provider;
import de.bytestore.pvheating.service.ModbusService;
import de.bytestore.pvheating.service.Pi4JService;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.flowui.component.virtuallist.JmixVirtualList;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@Route(value = "bindings-view", layout = MainView.class)
@ViewController("heater_BindingsView")
@ViewDescriptor("bindings-view.xml")
public class BindingsView extends StandardView {
    @Autowired
    private final ModbusService modbusService;

    @Autowired
    private final Pi4JService pi4JService;

    @ViewComponent
    private JmixVirtualList<Provider> providerItems;

    @Subscribe
    public void onInit(final InitEvent event) {
        providerItems.setItems(getProviders());
    }

    @Supply(to = "providerItems", subject = "renderer")
    private Renderer<Provider> providerItemsRenderer() {
        return new ComponentRenderer<>(object -> {
            VerticalLayout layoutIO = new VerticalLayout();

            layoutIO.add(new Span(object.getName()));
            layoutIO.add(new Span(object.getProvider()));

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
            providersIO.add(new Provider(keyIO, "modbus."));
        });

        return providersIO;
    }

}