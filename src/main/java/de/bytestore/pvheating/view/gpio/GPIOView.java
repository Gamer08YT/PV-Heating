package de.bytestore.pvheating.view.gpio;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.entity.GPIOType;
import de.bytestore.pvheating.objects.gpio.GPIOItem;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

import java.util.ArrayList;
import java.util.List;

@Route(value = "gpio", layout = MainView.class)
@ViewController("heater_GPIOView")
@ViewDescriptor("gpio-view.xml")
public class GPIOView extends StandardView {

    List<GPIOItem> gpioItems = new ArrayList<>() {{
        add(new GPIOItem(GPIOType.ANALOG, 0));
        add(new GPIOItem(GPIOType.DIGITAL, 0));
        add(new GPIOItem(GPIOType.DIGITAL, 1));
        add(new GPIOItem(GPIOType.DIGITAL, 2));
        add(new GPIOItem(GPIOType.DIGITAL, 3));
        add(new GPIOItem(GPIOType.DIGITAL, 4));
    }};

    @Subscribe
    public void onInit(final InitEvent event) {
        gpioItems.forEach(gpioItem -> {
            Button buttonIO = new Button((gpioItem.getType() == GPIOType.ANALOG ? "A" : "D") + gpioItem.getName());

            getContent().add(buttonIO);
        });
    }


}