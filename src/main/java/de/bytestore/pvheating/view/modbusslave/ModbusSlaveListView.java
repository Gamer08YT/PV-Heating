package de.bytestore.pvheating.view.modbusslave;

import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.entity.ModbusSlave;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.flowui.view.*;


@Route(value = "modbusSlaves", layout = MainView.class)
@ViewController("heater_ModbusSlave.list")
@ViewDescriptor("modbus-slave-list-view.xml")
@LookupComponent("modbusSlavesDataGrid")
@DialogMode(width = "64em")
public class ModbusSlaveListView extends StandardListView<ModbusSlave> {
}