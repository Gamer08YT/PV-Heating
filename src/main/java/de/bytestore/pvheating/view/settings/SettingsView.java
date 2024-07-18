package de.bytestore.pvheating.view.settings;


import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "settings-view", layout = MainView.class)
@ViewController("heater_SettingsView")
@ViewDescriptor("settings-view.xml")
public class SettingsView extends StandardView {
}