package de.bytestore.pvheating.view.modbusslave;

import com.fazecast.jSerialComm.SerialPort;
import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.entity.ModbusSlave;
import de.bytestore.pvheating.service.ModbusService;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.flowui.component.combobox.JmixComboBox;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

@Route(value = "modbusSlaves/:id", layout = MainView.class)
@ViewController("heater_ModbusSlave.detail")
@ViewDescriptor("modbus-slave-detail-view.xml")
@EditedEntityContainer("modbusSlaveDc")
public class ModbusSlaveDetailView extends StandardDetailView<ModbusSlave> {

    @Autowired
    private ModbusService modbusService;
    @ViewComponent
    private JmixComboBox<String> portField;

    @Subscribe
    public void onInit(final InitEvent event) {
        portField.setItems(Arrays.stream(modbusService.getSerialPorts())
                .map(SerialPort::getSystemPortName)
                .toArray(String[]::new));
    }
    
}