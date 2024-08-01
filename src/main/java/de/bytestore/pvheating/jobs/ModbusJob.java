package de.bytestore.pvheating.jobs;

import de.bytestore.pvheating.handler.CacheHandler;
import de.bytestore.pvheating.handler.ConfigHandler;
import de.bytestore.pvheating.objects.config.system.ModbusConfig;
import de.bytestore.pvheating.objects.config.system.SystemConfig;
import de.bytestore.pvheating.service.ModbusService;
import io.jmix.core.security.Authenticated;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ModbusJob implements Job {
    private SystemConfig config = ConfigHandler.getCached();

    @Autowired
    private ModbusService service;


//    public ModbusJob() {
//        if(config != null)
//            service.setConfig(config);
//    }

    @Authenticated
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // Only execute if not exceeded fail limit.
        if(service.getFails() <= 3) {
            queryValues();
        }
    }

    /**
     * This private method is used to query values from a Modbus slave device.
     * It checks if the Modbus configuration is enabled in the system configuration, and if so, it retrieves the Modbus configuration object.
     * Then, it iterates over all the sensors defined in the Modbus configuration and reads the input value from the specified slave device and address.
     * The read input value is logged as a debug message.
     *
     * @see ModbusJob
     * @see ModbusConfig
     * @see ModbusService
     * @see ModbusService#readInput(int, int, String)
     */
    private void queryValues() {
        if(config != null && config.getPower().getModbus() != null && config.getPower().getModbus().isEnabled()) {
            ModbusConfig modbusIO = config.getPower().getModbus();

            // Get all values from Slave.
            modbusIO.getSensors().forEach((keyIO, addressIO) -> {
                CacheHandler.setValue(keyIO, service.readInput(1, addressIO, modbusIO.getRegisterType()));
            });

        }
    }


}
