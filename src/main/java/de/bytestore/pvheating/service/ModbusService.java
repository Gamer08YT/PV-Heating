package de.bytestore.pvheating.service;

import com.fazecast.jSerialComm.SerialPort;
import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.util.BitVector;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import de.bytestore.pvheating.handler.ConfigHandler;
import de.bytestore.pvheating.objects.config.system.ModbusConfig;
import de.bytestore.pvheating.objects.config.system.SystemConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;


@Slf4j
@Service
public class ModbusService {
    private static ModbusSerialMaster masterIO;

    @Setter
    private SystemConfig config;

    @Getter
    private int fails = 0;

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
     * Reads input from a specific slave IO device.
     *
     * @param slaveIO the slave ID of the IO device to read from
     * @param startIO the starting address of the IO register to read
     * @param typeIO the type of IO register to read (e.g., float, int)
     * @return the value read from the IO register
     * @throws RuntimeException if there was an error reading the IO register
     */
    public Object readInput(int slaveIO, int startIO, String typeIO) {
        if(ModbusService.masterIO == null || !ModbusService.masterIO.isConnected())
            connect();

        // Convert Var Type to Register Size.
        int lengthIO = convertType(typeIO);

        float valueIO = 0;

        try {
            valueIO = readFloat32(slaveIO, startIO);
        } catch (ModbusException e) {
            throw new RuntimeException(e);
        }

        // Print Debug Message.
        log.info("Register " + startIO + " value (float32): " + valueIO);

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
     * Reads a 32-bit floating-point value from the specified slave ID and start address.
     *
     * @param slaveId       the ID of the slave device to read from
     * @param startAddress  the starting address of the 32-bit floating-point value in the slave device
     * @return the 32-bit floating-point value read from the slave device
     * @throws ModbusException  if there was an error during the Modbus operation
     */
    public static float readFloat32(int slaveId, int startAddress) throws ModbusException {
        InputRegister[] registers = ModbusService.masterIO.readInputRegisters(slaveId, startAddress, 2);
        int high = registers[0].getValue();
        int low = registers[1].getValue();
        return intToFloat32(high, low);
    }

    /**
     * Reads a 32-bit integer value from the specified slave device and starting address.
     *
     * @param slaveId the ID of the slave device
     * @param startAddress the starting address of the integer value
     * @return the 32-bit integer read from the slave device
     * @throws ModbusException if there is an error while reading the integer value
     */
    public static int readInt32(int slaveId, int startAddress) throws ModbusException {
        InputRegister[] registers = ModbusService.masterIO.readInputRegisters(slaveId, startAddress, 2);
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
    public static long readInt64(int slaveId, int startAddress) throws ModbusException {
        InputRegister[] registers = ModbusService.masterIO.readInputRegisters(slaveId, startAddress, 4);
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
        return ByteBuffer.wrap(new byte[] {
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
        if(config == null)
            config = ConfigHandler.getCached();

        if(config != null && config.getPower().getModbus() != null) {
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

                // Reset Fail Counter.
                fails = 0;
            } catch (Exception e) {
                if(fails == 2) {
                    log.error("Canceled Modus connection to {}, max attempts reached.", configIO.getPort());
                }

                // Increment Fail Counter.
                fails++;

                throw new RuntimeException(e);
            }
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
     * Returns the port number used for Modbus communication.
     * <p>
     * This method retrieves the port number from the configuration object if it exists. If the configuration object,
     * the power object, or the Modbus object is null, or if the port is not specified in the Modbus object,
     * it returns the string "Internal Error".
     * </p>
     *
     * @return the port number used for Modbus communication, or the string "Internal Error" if not found or configured incorrectly.
     */
    public String getPort() {
        if(config != null && config.getPower() != null && config.getPower().getModbus() != null)
            return config.getPower().getModbus().getPort();
        else
            return "Internal Error";
    }
}
