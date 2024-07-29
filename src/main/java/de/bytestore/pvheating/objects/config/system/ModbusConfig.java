package de.bytestore.pvheating.objects.config.system;

import lombok.Data;

import java.util.Map;

@Data
public class ModbusConfig {
    private boolean enabled = true;

    /**
     * The port variable represents the communication port used in the Modbus configuration.
     *
     * This variable is a string that specifies the communication port, such as a serial port or a network port.
     * The default port is set to "/dev/ttyUSB0", which is a commonly used port for serial communication on Linux systems.
     * This value can be adjusted based on the specific requirements of the system.
     *
     * @see ModbusConfig
     */
    private String port = "/dev/ttyUSB0";

    /**
     * The baud variable represents the baud rate used in the Modbus communication.
     *
     * The baud rate is a measure of the transmission speed in bits per second (bps) in a communication system.
     * It indicates how many bits can be transmitted per second over a communication channel.
     *
     * The default baud rate is set to 9600, which is a standard baud rate commonly used in Modbus communication.
     * This value can be adjusted based on the specific requirements of the system.
     *
     * @see ModbusConfig
     */
    private int baud = 9600;

    /**
     * The number of data bits used in the Modbus communication.
     *
     * This variable represents the number of data bits used in the Modbus communication. It is an integer value that indicates
     * the number of data bits per data packet.
     *
     * By default, the data bits are set to 8, indicating eight data bits per data packet.
     */
    private int dataBits = 8;

    /**
     * The number of stop bits used in the Modbus communication.
     *
     * This variable represents the number of stop bits used in the Modbus communication. It is an integer value that indicates
     * the number of stop bits per data packet.
     *
     * By default, the stop bits are set to 1, indicating one stop bit per data packet.
     */
    private int stopBits = 1;

    /**
     * The parity of a Modbus communication. It is an integer that represents the parity settings for the communication.
     * The value 0 indicates no parity. Other possible values may include:
     * - 1: odd parity
     * - 2: even parity
     * - 3: mark parity
     * - 4: space parity
     *
     * This variable is used in the ModbusConfig class to configure the parity settings for the Modbus communication.
     * By default, the parity is set to 0, indicating no parity.
     */
    private int parity = 0;

    /**
     * Define Register of Modbus Coil Size float32 => 2 Registers (1 Register => 16 Bits <--> [2 Bytes]).
     */
    private String registerType = "float";


    /**
     *
     */
    private Map<String, Integer> sensors = Map.of(
            "total-work", 72,
            "current-power", 52,
            "current-power-l1", 12,
            "current-power-l2", 14,
            "current-power-l3", 16
    );
}
