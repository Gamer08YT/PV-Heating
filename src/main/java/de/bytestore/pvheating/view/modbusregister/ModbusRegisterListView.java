package de.bytestore.pvheating.view.modbusregister;

import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.entity.ModbusRegister;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.flowui.view.*;


@Route(value = "modbusRegisters", layout = MainView.class)
@ViewController("heater_ModbusRegister.list")
@ViewDescriptor("modbus-register-list-view.xml")
@LookupComponent("modbusRegistersDataGrid")
@DialogMode(width = "64em")
public class ModbusRegisterListView extends StandardListView<ModbusRegister> {
}