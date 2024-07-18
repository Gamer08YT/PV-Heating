package de.bytestore.pvheating.view.settings;


import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.entity.SCRType;
import de.bytestore.pvheating.entity.SensorType;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.core.Messages;
import io.jmix.flowui.component.formlayout.JmixFormLayout;
import io.jmix.flowui.component.select.JmixSelect;
import io.jmix.flowui.view.*;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "settings-view", layout = MainView.class)
@ViewController("heater_SettingsView")
@ViewDescriptor("settings-view.xml")
public class SettingsView extends StandardView {

    private static final Logger log = LoggerFactory.getLogger(SettingsView.class);

    @ViewComponent
    private JmixSelect<Object> scrType;

    @Autowired
    private Messages messages;

    @ViewComponent
    private JmixSelect<Object> sensorType;
    @ViewComponent
    private JmixFormLayout currentRange;
    @ViewComponent
    private JmixFormLayout voltageRange;

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
    }

    private void initTemperature() {
        sensorType.setItems(SensorType.values());
    }

    private void initSCR() {
        scrType.setItems(SCRType.values());
    }

    @Supply(to = "sensorType", subject = "renderer")
    private ComponentRenderer sensorTypeRenderer() {
        return new ComponentRenderer(typeIO -> {
            SvgIcon iconIO = null;

            switch ((SensorType) typeIO) {
                case NTC -> iconIO = new SvgIcon("VAADIN/themes/PV-Heating/icons/negative.svg");
                case PTC -> iconIO = new SvgIcon("VAADIN/themes/PV-Heating/icons/positive.svg");
            }

            VerticalLayout layoutIO = new VerticalLayout(iconIO, new Span(messages.getMessage((SensorType) typeIO)));
            layoutIO.setAlignItems(FlexComponent.Alignment.CENTER);

            return layoutIO;
        });
    }


    @Supply(to = "scrType", subject = "renderer")
    private ComponentRenderer scrTypeRenderer() {
        return new ComponentRenderer(typeIO -> {
            SvgIcon iconIO = null;

            switch ((SCRType) typeIO) {
                case PWM -> iconIO = new SvgIcon("VAADIN/themes/PV-Heating/icons/pwm.svg");
                case CURRENT -> iconIO = new SvgIcon("VAADIN/themes/PV-Heating/icons/current.svg");
                case VOLTAGE -> iconIO = new SvgIcon("VAADIN/themes/PV-Heating/icons/voltage.svg");
            }

            VerticalLayout layoutIO = new VerticalLayout(iconIO, new Span(messages.getMessage((SCRType) typeIO)));
            layoutIO.setAlignItems(FlexComponent.Alignment.CENTER);

            return layoutIO;
        });
    }

    @Subscribe("scrType")
    public void onScrTypeComponentValueChange(final AbstractField.ComponentValueChangeEvent<JmixSelect<?>, ?> eventIO) {
        SCRType typeIO = (SCRType) scrType.getValue();

        currentRange.setVisible(typeIO == SCRType.CURRENT);
        voltageRange.setVisible(typeIO == SCRType.VOLTAGE);

        if (typeIO == SCRType.PWM)
            throw new NotImplementedException();
    }


}