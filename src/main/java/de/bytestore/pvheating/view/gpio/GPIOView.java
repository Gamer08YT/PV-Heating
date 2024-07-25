package de.bytestore.pvheating.view.gpio;


import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.handler.GPIOHandler;
import de.bytestore.pvheating.handler.devices.GPIOConfig;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.flowui.view.*;

import java.util.ArrayList;

@Route(value = "gpio", layout = MainView.class)
@ViewController("heater_GPIOView")
@ViewDescriptor("gpio-view.xml")
public class GPIOView extends StandardView {

    ArrayList<GPIOConfig> configs = GPIOHandler.getConfigs();

    @ViewComponent
    private HorizontalLayout gpioGrid;

    @Subscribe
    public void onInit(final InitEvent event) {
//        gpioItems.forEach(gpioItem -> {
//            Button buttonIO = new Button(gpioItem.getName());
//
//            gpioGrid.add(buttonIO);
//        });
    }


}