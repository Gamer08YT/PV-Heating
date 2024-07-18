package de.bytestore.pvheating.view.settings;


import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.flowui.component.tabsheet.JmixTabSheet;
import io.jmix.flowui.view.*;

@Route(value = "settings-view", layout = MainView.class)
@ViewController("heater_SettingsView")
@ViewDescriptor("settings-view.xml")
public class SettingsView extends StandardView {

    @Subscribe
    public void onInit(final InitEvent event) {
        initTabs();
    }

    private void initTabs() {
    }

}