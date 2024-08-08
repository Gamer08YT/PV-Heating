package de.bytestore.pvheating.view.homeassistantstates;

import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.entity.HomeAssistantStates;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.flowui.view.*;


@Route(value = "homeAssistantStateses", layout = MainView.class)
@ViewController("heater_HomeAssistantStates.list")
@ViewDescriptor("home-assistant-states-list-view.xml")
@LookupComponent("homeAssistantStatesesDataGrid")
@DialogMode(width = "64em")
public class HomeAssistantStatesListView extends StandardListView<HomeAssistantStates> {
}