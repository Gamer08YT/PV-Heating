package de.bytestore.pvheating.view.modbusregister;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.entity.ModbusRegister;
import de.bytestore.pvheating.handler.CacheHandler;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.core.Messages;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;


@Route(value = "modbusRegisters", layout = MainView.class)
@ViewController("heater_ModbusRegister.list")
@ViewDescriptor("modbus-register-list-view.xml")
@LookupComponent("modbusRegistersDataGrid")
@DialogMode(width = "64em")
public class ModbusRegisterListView extends StandardListView<ModbusRegister> {
    @ViewComponent
    private DataGrid<ModbusRegister> modbusRegistersDataGrid;
    @Autowired
    private MessageBundle messageBundle;
    @Autowired
    private Messages messages;

    @Subscribe
    public void onInit(final InitEvent event) {
        modbusRegistersDataGrid.addComponentColumn(homeAssistantStates -> {
            return new Span(String.valueOf(CacheHandler.getValueOrDefault("modbus." + homeAssistantStates.getName(), "n/a")));
        }).setHeader(messages.getMessage("de.bytestore.pvheating.view.homeassistantstates/value"));
    }
}