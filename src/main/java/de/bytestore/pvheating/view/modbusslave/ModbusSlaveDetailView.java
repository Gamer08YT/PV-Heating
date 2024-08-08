package de.bytestore.pvheating.view.modbusslave;

import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.entity.ModbusSlave;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "modbusSlaves/:id", layout = MainView.class)
@ViewController("heater_ModbusSlave.detail")
@ViewDescriptor("modbus-slave-detail-view.xml")
@EditedEntityContainer("modbusSlaveDc")
public class ModbusSlaveDetailView extends StandardDetailView<ModbusSlave> {
}