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
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.component.combobox.JmixComboBox;
import io.jmix.flowui.component.virtuallist.JmixVirtualList;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Slf4j
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
    @Autowired
    private MessageBundle messageBundle;
    @Autowired
    private Dialogs dialogs;

    @Subscribe
    public void onInit(final InitEvent event) {
        //System.out.println(providerService.getChildren());

        setItems();
        providerSelector.setItems(providerBeanService.getChildren());
        providerValue.setItems(getProviders());

        this.setProviderConfig();
    }

    /**
     * Sets the items for the providerItems based on the configuration obtained from the ConfigHandler.
     * If the provider configuration and the list of providers are not null, the providerItems are updated
     * with the list of providers obtained from the configuration.
     * <p>
     * Note: This method relies on the ConfigHandler class to obtain the provider configuration.
     * If the ConfigHandler class has not been initialized or the provider configuration is not available,
     * the providerItems will not be updated.
     */
    private void setItems() {
        if (ConfigHandler.getProviderConfig() != null && ConfigHandler.getProviderConfig().getProviders() != null)
            providerItems.setItems(ConfigHandler.getProviderConfig().getProviders());
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

            if (anyMatch) {
                newItems.add(provider);
            }
        });

        if (!newItems.isEmpty()) {
            providerValue.setEnabled(true);
            providerValue.setHelperText("");
            providerValue.setItems(newItems);
        } else {
            log.info("Disable");
            providerValue.setEnabled(false);
            providerValue.setHelperText(messageBundle.getMessage("noProviderFound"));
        }
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

    @Supply(to = "providerItems", subject = "renderer")
    private Renderer<Provider> providerItemsRenderer() {
        return new ComponentRenderer<>(object -> {
            HorizontalLayout layoutIO = new HorizontalLayout();
            layoutIO.setPadding(true);
            layoutIO.getStyle().setMarginBottom("0.25rem");
            layoutIO.addClassName("card");

            layoutIO.add(new Span(object.getType()));

            Arrays.stream(object.getTypes()).forEach(gpioType -> {
                Span badgeIO = new Span(gpioType.name());
                badgeIO.getElement().getThemeList().add("badge");
                badgeIO.getElement().getThemeList().add("success");

                layoutIO.add(badgeIO);
            });

            Span badgeIO = new Span(object.getName());
            badgeIO.getElement().getThemeList().add("badge");
            badgeIO.getElement().getThemeList().add("normal");

            layoutIO.add(badgeIO);

            return layoutIO;
        });
    }

    @Supply(to = "providerValue", subject = "renderer")
    private Renderer<Provider> providerValueRenderer() {
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
        if (pi4JService.getWire1fails() < 3) {
            pi4JService.get1WireDevices().forEach(deviceItem -> {
                providersIO.add(new Provider(deviceItem, "wire1", new GPIOType[]{
                        GPIOType.WIRE1
                }));
            });
        }

        modbusService.getRegisters().forEach(modbusRegister -> {
            providersIO.add(new Provider(modbusRegister.getName(), "modbus", new GPIOType[]{
                    GPIOType.MODBUS
            }));
        });

        return providersIO;
    }

    @Subscribe(id = "providerSave", subject = "clickListener")
    public void onProviderSaveClick(final ClickEvent<JmixButton> event) {
        if (providerSelector.getValue() != null && providerValue.getValue() != null && ConfigHandler.getProviderConfig() != null) {
            ConfigHandler.getProviderConfig().setProvider(providerSelector.getValue().name(), providerValue.getValue(), providerSelector.getValue().name());
            ConfigHandler.saveProvider();

            log.info("Saved Binding {}.", providerSelector.getValue().name());

            setItems();
        } else
            dialogs.createMessageDialog().withText(messageBundle.formatMessage("cantSaveBinding", providerSelector.getValue().name())).open();
    }

}