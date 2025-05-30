package de.bytestore.pvheating.service;

import com.fazecast.jSerialComm.SerialPort;
import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.util.BitVector;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import de.bytestore.pvheating.entity.ModbusRegister;
import de.bytestore.pvheating.entity.ModbusSlave;
import io.jmix.core.DataManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
public class ModbusService {
    private static HashMap<UUID, ModbusSerialMaster> masterIO = new HashMap<UUID, ModbusSerialMaster>();

    private static final int BAUD_RATE = 9600;
    private static final int DATA_BITS = 8;
    private static final int STOP_BITS = 1;
    private static final int PARITY = 0;
    private final DataManager dataManager;

    @Getter
    private int fails = 0;

    public ModbusService(DataManager dataManager) {
        this.dataManager = dataManager;
    }

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
     *
     */
    public Object readInput(ModbusSlave converterIO, int slaveIO, int startIO, String typeIO) {
        // Convert Var Type to Register Size.
        int lengthIO = convertType(typeIO);

        float valueIO = 0;

        try {
            valueIO = readFloat32(converterIO, slaveIO, startIO);
        } catch (ModbusException e) {
            throw new RuntimeException(e);
        }

        // Print Debug Message.
        log.debug("Register {} value (float32): {}", startIO, valueIO);

        return valueIO;
//        if (registers != null && registers.length == lengthIO) {
//            // Convert to a float
//            switch (typeIO.toLowerCase()) {
//                case "float":
//                    // Combine the two registers into a single 32-bit float
//                    int high = registers[0].getValue();
//                    int low = registers[1].getValue();
//
//                    return intToFloat32(high, low);
//                break;
//            }
//
//
//            // Print Debug Message.
//            log.info("Register " + startIO + " value (float32): " + );
//        } else {
//            // Print Error Message.
//            log.error("Failed to read the required number of registers.");
//        }
    }

    /**
     * Reads a 32-bit floating point value from the specified modbus slave.
     *
     * @param converterIO
     * @param slaveId      the ID of the modbus slave device
     * @param startAddress the starting address of the registers to read
     * @return the 32-bit floating point value read from the slave device
     * @throws ModbusException if there is an error communicating with the modbus slave device
     */
    public float readFloat32(ModbusSlave converterIO, int slaveId, int startAddress) throws ModbusException {
        if (!ModbusService.masterIO.containsKey(converterIO.getId()) || !ModbusService.masterIO.get(converterIO.getId()).isConnected())
            connect(converterIO);

        try {
            if (masterIO.size() > 0) {
                InputRegister[] registers = ModbusService.masterIO.get(converterIO.getId()).readInputRegisters(slaveId, startAddress, 2);
                int high = registers[0].getValue();
                int low = registers[1].getValue();
                return intToFloat32(high, low);
            }
        } catch (ModbusException exceptionIO) {
            // Do nothing.
            log.error(exceptionIO.getMessage());
        }

        return -1;
    }

    /**
     * Reads a 32-bit integer value from the specified slave device and starting address.
     *
     * @param slaveId the ID of the slave device
     * @param startAddress the starting address of the integer value
     * @return the 32-bit integer read from the slave device
     * @throws ModbusException if there is an error while reading the integer value
     */
    public static int readInt32(ModbusSlave converterIO, int slaveId, int startAddress) throws ModbusException {
        InputRegister[] registers = ModbusService.masterIO.get(converterIO.getId()).readInputRegisters(slaveId, startAddress, 2);
        int high = registers[0].getValue();
        int low = registers[1].getValue();
        return intToInt32(high, low);
    }

    /**
     * Reads a 64-bit (8-byte) integer value from the Modbus master input registers.
     *
     * @param slaveId       the ID of the slave device to communicate with
     * @param startAddress  the starting address of the input registers to read from
     * @return the 64-bit integer value read from the input registers
     * @throws ModbusException if a Modbus exception occurred during the communication process
     */
    public static long readInt64(ModbusSlave converterIO, int slaveId, int startAddress) throws ModbusException {
        InputRegister[] registers = ModbusService.masterIO.get(converterIO.getId()).readInputRegisters(slaveId, startAddress, 4);
        int highHigh = registers[0].getValue();
        int highLow = registers[1].getValue();
        int lowHigh = registers[2].getValue();
        int lowLow = registers[3].getValue();
        return intToInt64(highHigh, highLow, lowHigh, lowLow);
    }

    /**
     * Converts two given integer values to a 32-bit floating point number.
     *
     * @param high the high 16 bits of the integer value
     * @param low the low 16 bits of the integer value
     * @return the converted 32-bit floating point number
     */
    private static float intToFloat32(int high, int low) {
        int combined = (high << 16) | (low & 0xFFFF);
        return ByteBuffer.wrap(new byte[]{
                (byte) (combined >> 24),
                (byte) (combined >> 16),
                (byte) (combined >> 8),
                (byte) combined
        }).getFloat();
    }

    /**
     * Converts two 16-bit integers, `high` and `low`, into a 32-bit integer value.
     *
     * @param high the high 16 bits of the 32-bit value.
     * @param low the low 16 bits of the 32-bit value.
     *
     * @return the resulting 32-bit integer value obtained by combining the high and low bits.
     */
    private static int intToInt32(int high, int low) {
        return (high << 16) | (low & 0xFFFF);
    }

    /**
     * Converts four 32-bit signed integers to a single 64-bit signed integer using bit shifting and bitwise OR operations.
     *
     * @param highHigh the most significant bits of the high 32 bits of the 64-bit integer.
     * @param highLow the least significant bits of the high 32 bits of the 64-bit integer.
     * @param lowHigh the most significant bits of the low 32 bits of the 64-bit integer.
     * @param lowLow the least significant bits of the low 32 bits of the 64-bit integer.
     * @return the resulting 64-bit signed integer.
     */
    private static long intToInt64(int highHigh, int highLow, int lowHigh, int lowLow) {
        return ((long) highHigh << 48) | ((long) highLow << 32) | ((long) lowHigh << 16) | (lowLow & 0xFFFF);
    }

    /**
     * Converts the given input/output type to the corresponding type in bits.
     *
     * @param typeIO the input/output type to be converted (e.g., "int", "float", "long")
     * @return the corresponding type in bits, or -1 if the type is not supported
     */
    private int convertType(String typeIO) {
        switch (typeIO.toLowerCase()) {
            case "int":
                return 32;
            case "float":
                return 32;
            case "long":
                return 64;
        }

        return -1;
    }

    /**
     * Retrieves the value from the Modbus slave device.
     *
     * @param addressIO the starting address of the coils to read from
     * @param countIO the number of coils to read
     * @return a BitVector object representing the value retrieved from the Modbus slave device
     * @throws RuntimeException if an exception occurs during the retrieval process
     */
    public BitVector getValue(ModbusSlave converterIO, int addressIO, int countIO) {
        if (ModbusService.masterIO == null || !ModbusService.masterIO.get(converterIO.getId()).isConnected())
            connect(converterIO);

        try {
            return ModbusService.masterIO.get(converterIO.getId()).readCoils(addressIO, countIO);
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
    public void connect(ModbusSlave converterIO) {
        // Create new Serial Config.
        SerialParameters parametersIO = new SerialParameters();

        parametersIO.setPortName(converterIO.getPort());
        parametersIO.setBaudRate(converterIO.getBaud());
        parametersIO.setDatabits(converterIO.getDataBits());
        parametersIO.setStopbits(converterIO.getStopBits());
        parametersIO.setParity(parametersIO.getParity());

        // Create new Master Instance.
        masterIO.put(converterIO.getId(), new ModbusSerialMaster(parametersIO));

        try {
            // Connect to Slave.
            ModbusService.masterIO.get(converterIO.getId()).connect();

            // Print Success Message.
            log.info("Connected to Modbus via {}.", converterIO.getPort());

            // Reset Fail Counter.
            fails = 0;
        } catch (Exception e) {
            if (fails == 2) {
                log.error("Canceled Modus connection to {}, max attempts reached.", converterIO.getPort());
            }

            // Increment Fail Counter.
            fails++;

            //throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves an array of available serial ports.
     *
     * @return an array of SerialPort objects representing the available serial ports
     */
    public SerialPort[] getSerialPorts() {
        return SerialPort.getCommPorts();
    }

    /**
     * Resets the number of failed attempts to zero.
     */
    public void resetFails() {
        fails = 0;

        // Print Info about resetting failed attempts.
        log.info("Resetting failed attempts.");
    }

    /**
     * Lists the Modbus slave addresses that are responding on the specified communication port within the specified address range.
     *
     * @param commPortId   the ID of the communication port to use
     * @param startAddress the starting address to check for slave responses
     * @param endAddress   the ending address to check for slave responses
     * @return a list of slave addresses that responded within the specified range
     */
    public static List<Integer> listModbusSlaves(String commPortId, int startAddress, int endAddress) {
        List<Integer> slaveAddresses = new ArrayList<>();

        SerialParameters params = new SerialParameters();
        params.setPortName(commPortId);
        params.setBaudRate(BAUD_RATE);
        params.setDatabits(DATA_BITS);
        params.setStopbits(STOP_BITS);
        params.setParity(PARITY);
        params.setEncoding("rtu");
        params.setEcho(false);

        ModbusSerialMaster master = new ModbusSerialMaster(params);

        try {
            master.connect();

            for (int address = startAddress; address <= endAddress; address++) {
                try {
                    // Read input registers to check if the slave responds
                    InputRegister[] registers = master.readInputRegisters(address, 0, 1);

                    if (registers != null && registers.length > 0) {
                        slaveAddresses.add(address);
                        System.out.println("Valid response from slave at address " + address);
                    }
                } catch (ModbusException e) {
                    // No response from this address
                    System.out.println("No response from slave at address " + address);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                master.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return slaveAddresses;
    }


    /**
     *
     */
    public List<ModbusRegister> getRegisters() {
        return dataManager.load(ModbusRegister.class).all().list();
    }
}