package de.bytestore.pvheating.view.homeassistantstates;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.entity.HomeAssistantStates;
import de.bytestore.pvheating.handler.CacheHandler;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;


@Route(value = "homeAssistantStateses", layout = MainView.class)
@ViewController("heater_HomeAssistantStates.list")
@ViewDescriptor("home-assistant-states-list-view.xml")
@LookupComponent("homeAssistantStatesesDataGrid")
@DialogMode(width = "64em")
public class HomeAssistantStatesListView extends StandardListView<HomeAssistantStates> {
    @ViewComponent
    private DataGrid<HomeAssistantStates> homeAssistantStatesesDataGrid;
    @Autowired
    private MessageBundle messageBundle;

    @Subscribe
    public void onInit(final InitEvent event) {
        homeAssistantStatesesDataGrid.addComponentColumn(homeAssistantStates -> {
            return new Span(String.valueOf(CacheHandler.getValueOrDefault("homeassistant." + homeAssistantStates.getName(), "n/a")));
        }).setHeader(messageBundle.getMessage("value"));
    }

}