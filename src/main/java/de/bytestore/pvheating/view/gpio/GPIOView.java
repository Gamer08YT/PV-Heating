package de.bytestore.pvheating.view.gpio;


import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.handler.ConfigHandler;
import de.bytestore.pvheating.handler.GPIOHandler;
import de.bytestore.pvheating.objects.config.GPIOConfig;
import de.bytestore.pvheating.objects.gpio.GPIOItem;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.core.Messages;
import io.jmix.flowui.component.formlayout.JmixFormLayout;
import io.jmix.flowui.component.select.JmixSelect;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Route(value = "gpio", layout = MainView.class)
@ViewController("heater_GPIOView")
@ViewDescriptor("gpio-view.xml")
public class GPIOView extends StandardView {

    @ViewComponent
    private FormLayout gpioGrid;

    @Autowired
    private Messages messages;
    @ViewComponent
    private JmixFormLayout selector;

    private GPIOItem selected = null;

    @ViewComponent
    private JmixSelect<String> selectType;

    private GPIOConfig config;

    @Subscribe
    public void onInit(final InitEvent event) {
        System.out.println(GPIOHandler.getConfigs());

        ConfigHandler.readGPIO();
        config = ConfigHandler.getGpioConfig();


        GPIOHandler.getConfigs().forEach(entryIO -> {
            HorizontalLayout layoutIO = new HorizontalLayout();
            layoutIO.setWidthFull();
            layoutIO.setAlignItems(FlexComponent.Alignment.CENTER);
            layoutIO.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
            layoutIO.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

            entryIO.getPins().forEach(pinIO -> {
                Button gpioIO = new Button(pinIO.getName());
                gpioIO.addClassName("gpio");

                StringBuilder tooltipIO = new StringBuilder();
                AtomicInteger countIO = new AtomicInteger(1);

                gpioIO.addClickListener(buttonClickEvent -> {
                    this.selected = pinIO;

                    this.handleSelector();
                });

                // Append Tooltips.
                pinIO.getType().forEach(gpioType -> {
                    tooltipIO.append(messages.getMessage(gpioType) + (pinIO.getType().size() > 1 ? (countIO.get() < pinIO.getType().size() ? "," : "") :"") + " ");

                    countIO.getAndIncrement();
                });

                // Add Tooltip.
                Tooltip.forComponent(gpioIO).withText(tooltipIO.toString());

                layoutIO.add(gpioIO);
            });

            gpioGrid.add(layoutIO);
        });
//        if(configs != null && configs.size() > 0) {
//            configs.forEach(configItem -> {
//                Button buttonIO = new Button(configItem.getClass().getName());
//
//                gpioGrid.add(buttonIO);
//            });
//        }
    }

    /**
     * Handles the selector component in the GPIOView.
     * Enables the selector, adds items to the selectType dropdown based on the selected GPIOItem's supported GPIOListeners.
     */
    private void handleSelector() {
        selector.setEnabled(true);

        ArrayList<String> listeners = new ArrayList<String>();

        GPIOHandler.getListeners().forEach(gpioListener -> {
            if(this.selected != null && this.selected.getType().contains(gpioListener.type())) {
                listeners.add(gpioListener.name());
            }
        });


        // Check if Config Entry exists.
        if(!config.getDefinitions().isEmpty()) {
            // Read Value from Config.
            String valueIO = config.getDefinitions().get(0).getPinByName(selected.getName()).getDefinition();

            // Check if Value is not empty.
            if (valueIO != null && valueIO != "") {
                selectType.setValue(valueIO);
            }
        }

        // Set Items of Select.
        selectType.setItems(listeners);

    }

    @Subscribe(id = "assignBtn", subject = "clickListener")
    public void onAssignBtnClick(final ClickEvent<JmixButton> event) {
        // Check if Config Entry exists.
        if(!config.getDefinitions().isEmpty()) {
            config.getDefinitions().get(0).getPinByName(selected.getName()).setDefinition(selectType.getValue());
        }

        // Save Config to Disk.
        ConfigHandler.saveGPIO();

        // Disable Selector.
        selector.setEnabled(false);
    }

}