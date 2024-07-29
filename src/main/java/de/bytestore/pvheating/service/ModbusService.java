package de.bytestore.pvheating.service;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster;
import com.ghgande.j2mod.modbus.slave.ModbusSlaveFactory;
import com.ghgande.j2mod.modbus.util.BitVector;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import de.bytestore.pvheating.handler.ConfigHandler;
import de.bytestore.pvheating.objects.config.system.ModbusConfig;
import de.bytestore.pvheating.objects.config.system.SystemConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class ModbusService {
    private static ModbusSerialMaster masterIO;

    private SystemConfig config = ConfigHandler.getCached();

    // HA Example:
    //    name: sdm72dm
    //    type: serial
    //    method: rtu
    //    port: /dev/ttyUSB0
    //    baudrate: 9600
    //    stopbits: 1
    //    bytesize: 8
    //    parity: N

    /**
     * Retrieves the value from the Modbus slave device.
     *
     * @param addressIO the starting address of the coils to read from
     * @param countIO the number of coils to read
     * @return a BitVector object representing the value retrieved from the Modbus slave device
     * @throws RuntimeException if an exception occurs during the retrieval process
     */
    public BitVector getValue(int addressIO, int countIO) {
        if(ModbusService.masterIO == null || !ModbusService.masterIO.isConnected())
            connect();

        try {
            return ModbusService.masterIO.readCoils(addressIO, countIO);
        } catch (ModbusException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Connects to the Modbus slave device using the given configuration.
     * This method initializes a new Serial Config, sets the port name, baud rate, data bits,
     * stop bits, and parity from the ModbusConfig object obtained from the SystemConfig object.
     * Then it creates a new ModbusSerialMaster instance with the SerialParameters object.
     * Finally, it connects to the slave device using the master instance.
     * If any exception occurs during the connection, it throws a RuntimeException.
     */
    public void connect() {
        ModbusConfig configIO = config.getPower().getModbus();

        // Create new Serial Config.
        SerialParameters parametersIO = new SerialParameters();

        parametersIO.setPortName(configIO.getPort());
        parametersIO.setBaudRate(configIO.getBaud());
        parametersIO.setDatabits(configIO.getDataBits());
        parametersIO.setStopbits(configIO.getStopBits());
        parametersIO.setParity(parametersIO.getParity());

        // Create new Master Instance.
        ModbusService.masterIO = new ModbusSerialMaster(parametersIO);

        try {
            // Connect to Slave.
            ModbusService.masterIO.connect();

            // Print Success Message.
            log.info("Connected to Modbus via {}.", configIO.getPort());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
