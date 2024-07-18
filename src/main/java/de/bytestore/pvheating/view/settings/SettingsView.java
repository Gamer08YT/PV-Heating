package de.bytestore.pvheating.view.settings;


import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.FontIcon;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoIcon;
import de.bytestore.pvheating.entity.SCRType;
import de.bytestore.pvheating.entity.SensorType;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.core.Messages;
import io.jmix.flowui.component.select.JmixSelect;
import io.jmix.flowui.component.tabsheet.JmixTabSheet;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "settings-view", layout = MainView.class)
@ViewController("heater_SettingsView")
@ViewDescriptor("settings-view.xml")
public class SettingsView extends StandardView {

    @ViewComponent
    private JmixSelect<Object> scrType;
    @Autowired
    private Messages messages;
    @ViewComponent
    private JmixSelect<Object> sensorType;

    @Subscribe
    public void onInit(final InitEvent event) {
        initTabs();
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


}