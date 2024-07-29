package de.bytestore.pvheating.jobs;

import com.fazecast.jSerialComm.SerialPort;
import de.bytestore.pvheating.handler.ConfigHandler;
import de.bytestore.pvheating.objects.config.system.ModbusConfig;
import de.bytestore.pvheating.objects.config.system.SystemConfig;
import de.bytestore.pvheating.service.ModbusService;
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

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        queryValues();
    }

    private void queryValues() {
        if(config != null) {
            ModbusConfig modbusIO = config.getPower().getModbus();

            // Get all values from Slave.
            modbusIO.getSensors().forEach((keyIO, addressIO) -> {
                service.readInput(1, addressIO, modbusIO.getRegisterType());
            });

        }
    }


}
