package de.bytestore.pvheating.view.stats;


import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "stats-view", layout = MainView.class)
@ViewController("heater_StatsView")
@ViewDescriptor("stats-view.xml")
public class StatsView extends StandardView {
}