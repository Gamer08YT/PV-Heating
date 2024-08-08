package de.bytestore.pvheating.view.modbusregister;

import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.entity.ModbusRegister;
import de.bytestore.pvheating.entity.ModbusSlave;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.core.DataManager;
import io.jmix.flowui.component.combobox.JmixComboBox;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "modbusRegisters/:id", layout = MainView.class)
@ViewController("heater_ModbusRegister.detail")
@ViewDescriptor("modbus-register-detail-view.xml")
@EditedEntityContainer("modbusRegisterDc")
public class ModbusRegisterDetailView extends StandardDetailView<ModbusRegister> {
    @ViewComponent
    private JmixComboBox<ModbusSlave> slaveField;
    @Autowired
    private DataManager dataManager;

    @Subscribe
    public void onInit(final InitEvent event) {
        slaveField.setItems(dataManager.load(ModbusSlave.class).all().list());
    }

}