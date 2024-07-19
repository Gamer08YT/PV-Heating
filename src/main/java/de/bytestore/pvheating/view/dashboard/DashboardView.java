package de.bytestore.pvheating.view.dashboard;


import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "dashboard", layout = MainView.class)
@ViewController("heater_DashboardView")
@ViewDescriptor("dashboard-view.xml")
public class DashboardView extends StandardView {
}