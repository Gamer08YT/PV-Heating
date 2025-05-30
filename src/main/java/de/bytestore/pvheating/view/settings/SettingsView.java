package de.bytestore.pvheating.view.settings;


import com.fazecast.jSerialComm.SerialPort;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.entity.SCRType;
import de.bytestore.pvheating.entity.SensorType;
import de.bytestore.pvheating.handler.ConfigHandler;
import de.bytestore.pvheating.objects.config.system.SystemConfig;
import de.bytestore.pvheating.service.ModbusService;
import de.bytestore.pvheating.service.Pi4JService;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.core.Messages;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.checkbox.JmixCheckbox;
import io.jmix.flowui.component.combobox.JmixComboBox;
import io.jmix.flowui.component.formlayout.JmixFormLayout;
import io.jmix.flowui.component.textfield.JmixNumberField;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static de.bytestore.pvheating.entity.SensorType.NTC;
import static de.bytestore.pvheating.entity.SensorType.PTC;

@Route(value = "settings", layout = MainView.class)
@ViewController("heater_SettingsView")
@ViewDescriptor("settings-view.xml")
public class SettingsView extends StandardView {

    private static final Logger log = LoggerFactory.getLogger(SettingsView.class);

    @ViewComponent
    private JmixComboBox<SCRType> scrType;

    @Autowired
    private Pi4JService pi4JService;

    @Autowired
    private ModbusService modbusService;

    @Autowired
    private Messages messages;

    @ViewComponent
    private JmixComboBox<SensorType> sensorType;

    @ViewComponent
    private JmixFormLayout currentRange;

    @ViewComponent
    private JmixFormLayout voltageRange;

    @Autowired
    private MessageBundle messageBundle;

    private SystemConfig config = ConfigHandler.getCached();

    @ViewComponent
    private JmixNumberField currentMaxField;
    @ViewComponent
    private JmixNumberField currentMinField;
    @ViewComponent
    private JmixNumberField voltageMinField;
    @ViewComponent
    private JmixNumberField voltageMaxField;
    @ViewComponent
    private JmixNumberField desiredTemperature;
    @ViewComponent
    private JmixNumberField sensorResistance;
    @ViewComponent
    private JmixCheckbox checkboxPump;
    @ViewComponent
    private JmixNumberField minPowerUsage;
    @ViewComponent
    private JmixNumberField maxPowerUsage;
    @ViewComponent
    private JmixButton save;
    @Autowired
    private Notifications notifications;
    @ViewComponent
    private JmixNumberField offsetPowerUsage;
    @ViewComponent
    private ComboBox<String> wire1Device;
    @ViewComponent
    private VerticalLayout oneWire;
    @ViewComponent
    private JmixFormLayout analogSensors;
    @ViewComponent
    private Span oneWireError;
    @ViewComponent
    private ComboBox<String> modbusConverter;

    @Subscribe
    public void onInit(final InitEvent event) {
        initTabs();
    }

    @Subscribe("tabSheet")
    public void onTabSheetSelectedChange(final Tabs.SelectedChangeEvent eventIO) {
        // Print Info Message.
        log.info("Showing Settings Tab {}.", eventIO.getSelectedTab().getId().get());

        getContent().findComponent("sheet" + eventIO.getSelectedTab().getId().get()).ifPresent(component -> {
            eventIO.getPreviousTab().getId().ifPresent(idIO -> {
                getContent().getComponent("sheet" + idIO).setVisible(false);
            });

            ((FormLayout) component).setVisible(true);
        });
    }

    private void initTabs() {
        initTemperature();
        initSCR();
        initPump();
        initPower();
    }

    /**
     * Initializes the power usage values based on the configuration.
     * Sets the minimum power usage and the maximum power usage values.
     * Uses the minPower and maxPower properties of the PowerConfig class.
     */
    private void initPower() {
        modbusConverter.setItems(Arrays.stream(modbusService.getSerialPorts())
                .map(SerialPort::getSystemPortName)
                .toArray(String[]::new));
        modbusConverter.setValue(config.getPower().getModbus().getPort());

        minPowerUsage.setValue(config.getPower().getMinPower());
        maxPowerUsage.setValue(config.getPower().getMaxPower());
        offsetPowerUsage.setValue(config.getPower().getOffsetPower());
    }

    private void initPump() {
        checkboxPump.setValue(config.getPump().isEnable());
    }

    /**
     * Initializes the temperature configuration.
     * This method sets the sensor type, resistance, and desired temperature based on the configuration.
     *
     * The sensor type is set by populating the items in the sensorType ComboBox with the available SensorType values,
     * and then setting the value of sensorType to the sensor type from the configuration.
     *
     * The resistance value is set by retrieving the corresponding value from the configuration
     * and then setting the value of sensorResistance.
     *
     * The desired temperature value is set by retrieving the corresponding value from the configuration
     * and then setting the value of desiredTemperature.
     */
    private void initTemperature() {
        sensorType.setItems(SensorType.values());
        sensorType.setValue(config.getTemperature().getSensorType());

        // Set Sensor Values.
        sensorResistance.setValue(config.getTemperature().getResistance());
        desiredTemperature.setValue(config.getTemperature().getDesiredTemperature());

        try {
            List<String> wire1Devices = pi4JService.get1WireDevices();

            if(wire1Devices.size() > 0) {
                wire1Device.setItems(wire1Devices);

                // Set 1Wire Devices.
                wire1Device.setValue(config.getTemperature().getWire1Device());
            } else
                oneWireError.setVisible(true);
        } catch (Exception exceptionIO) {
            // todo: Show 1Wire Dialog.
            oneWireError.setVisible(true);
        }

        setTemperatureSelectorFields();
    }

    @Subscribe("sensorType")
    public void onSensorTypeComponentValueChange(final AbstractField.ComponentValueChangeEvent<JmixComboBox<?>, ?> event) {
        setTemperatureSelectorFields();
    }

    /**
     * Sets the visibility of temperature selector fields based on the sensor type.
     * If the sensor type is NTC or PTC, the oneWire field is hidden and the analogSensors field is displayed.
     * If the sensor type is DS18B20, the oneWire field is displayed and the analogSensors field is hidden.
     */
    private void setTemperatureSelectorFields() {
        if(sensorType.getValue() == NTC || sensorType.getValue() == PTC) {
            analogSensors.setVisible(true);
            oneWire.setVisible(false);
        } else if(sensorType.getValue() == SensorType.DS18B20) {
            oneWire.setVisible(true);
            analogSensors.setVisible(false);
        }
    }

    /**
     * Initializes the SCR (Silicon-Controlled Rectifier) configuration.
     * This method sets the SCR type, voltage values, and current values based on the configuration.
     *
     * The SCR type is set by populating the items in the scrType ComboBox with the available SCRType values,
     * and then setting the value of scrType to the SCR type from the configuration.
     *
     * The minimum and maximum voltage values are set by retrieving the corresponding values from the configuration
     * and then setting the values of voltageMinField and voltageMaxField respectively.
     *
     * The maximum and minimum current values are set by retrieving the corresponding values from the configuration
     * and then setting the values of currentMaxField and currentMinField respectively.
     */
    private void initSCR() {
        scrType.setItems(SCRType.values());
        scrType.setValue(config.getScr().getType());

        // Set Voltage Values.
        voltageMinField.setValue(config.getScr().getMinVoltage());
        voltageMaxField.setValue(config.getScr().getMaxVoltage());

        // Set Current Values.
        currentMaxField.setValue(config.getScr().getMaxCurrent());
        currentMinField.setValue(config.getScr().getMinCurrent());
    }

    @Supply(to = "sensorType", subject = "renderer")
    private Renderer<SensorType> sensorTypeRenderer() {
        return new ComponentRenderer<Component, SensorType>(typeIO -> {
            SvgIcon iconIO = null;

            switch (typeIO) {
                case NTC -> iconIO = new SvgIcon("VAADIN/themes/PV-Heating/icons/negative.svg");
                case PTC -> iconIO = new SvgIcon("VAADIN/themes/PV-Heating/icons/positive.svg");
                default -> iconIO = new SvgIcon("VAADIN/themes/PV-Heating/icons/chip.svg");
            }

            VerticalLayout layoutIO = new VerticalLayout(iconIO, new Span(messages.getMessage(typeIO)));
            layoutIO.setAlignItems(FlexComponent.Alignment.CENTER);

            return layoutIO;
        });
    }


    @Supply(to = "scrType", subject = "renderer")
    private Renderer<SCRType> scrTypeRenderer() {
        return new ComponentRenderer<Component, SCRType>(typeIO -> {
            SvgIcon iconIO = null;

            switch (typeIO) {
                case PWM -> iconIO = new SvgIcon("VAADIN/themes/PV-Heating/icons/pwm.svg");
                case CURRENT -> iconIO = new SvgIcon("VAADIN/themes/PV-Heating/icons/current.svg");
                case VOLTAGE -> iconIO = new SvgIcon("VAADIN/themes/PV-Heating/icons/voltage.svg");
            }

            VerticalLayout layoutIO = new VerticalLayout(iconIO, new Span(messages.getMessage(typeIO)));
            layoutIO.setAlignItems(FlexComponent.Alignment.CENTER);

            return layoutIO;
        });
    }

    @Subscribe("scrType")
    public void onScrTypeComponentValueChange(final AbstractField.ComponentValueChangeEvent<JmixComboBox<?>, ?> eventIO) {
        SCRType typeIO = (SCRType) scrType.getValue();

        currentRange.setVisible(typeIO == SCRType.CURRENT);
        voltageRange.setVisible(typeIO == SCRType.VOLTAGE);

//        if (typeIO != SCRType.PWM)
//            throw new NotImplementedException(messageBundle.getMessage("pwmNotImplemented"));
    }

    @Subscribe(id = "save", subject = "clickListener")
    public void onSaveClick(final ClickEvent<JmixButton> event) {
        config.getPower().setMaxPower(maxPowerUsage.getValue());
        config.getPower().setMinPower(minPowerUsage.getValue());
        config.getPower().setOffsetPower(offsetPowerUsage.getValue());

        config.getPump().setEnable(checkboxPump.getValue());

        config.getScr().setMaxCurrent(currentMaxField.getValue());
        config.getScr().setMinCurrent(currentMinField.getValue());

        config.getScr().setMaxVoltage(voltageMaxField.getValue());
        config.getScr().setMinVoltage(voltageMinField.getValue());

        config.getScr().setType((SCRType) scrType.getValue());

        config.getTemperature().setSensorType((SensorType) sensorType.getValue());
        config.getTemperature().setResistance(sensorResistance.getValue());
        config.getTemperature().setDesiredTemperature(desiredTemperature.getValue());
        config.getTemperature().setWire1Device(wire1Device.getValue());

        ConfigHandler.saveConfig();
        save.setEnabled(true);

        notifications.show(messageBundle.getMessage("settingsSaved"));
    }



}