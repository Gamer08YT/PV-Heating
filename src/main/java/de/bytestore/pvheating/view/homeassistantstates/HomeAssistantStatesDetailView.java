package de.bytestore.pvheating.view.homeassistantstates;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.entity.HomeAssistantStates;
import de.bytestore.pvheating.handler.HAHandler;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.flowui.component.combobox.JmixComboBox;
import io.jmix.flowui.view.*;

@Route(value = "homeAssistantStateses/:id", layout = MainView.class)
@ViewController("heater_HomeAssistantStates.detail")
@ViewDescriptor("home-assistant-states-detail-view.xml")
@EditedEntityContainer("homeAssistantStatesDc")
public class HomeAssistantStatesDetailView extends StandardDetailView<HomeAssistantStates> {
    @ViewComponent
    private JmixComboBox<String> nameField;

    @Subscribe
    public void onInit(final InitEvent event) {
        nameField.setItems(HAHandler.getAllSensors());
    }
//
//    @Supply(to = "nameField", subject = "renderer")
//    private Renderer<JsonObject> nameFieldRenderer() {
//        return new ComponentRenderer<>(entityIO -> new Span( entityIO.get("entity_id").getAsString() + " ("+ entityIO.get("state").getAsString()+")"));
//    }
    
}