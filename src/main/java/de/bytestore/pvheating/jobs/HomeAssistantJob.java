package de.bytestore.pvheating.jobs;

import de.bytestore.pvheating.entity.HomeAssistantStates;
import de.bytestore.pvheating.handler.CacheHandler;
import de.bytestore.pvheating.handler.ConfigHandler;
import de.bytestore.pvheating.handler.HAHandler;
import de.bytestore.pvheating.objects.config.system.ModbusConfig;
import de.bytestore.pvheating.objects.config.system.SystemConfig;
import de.bytestore.pvheating.service.ModbusService;
import io.jmix.core.DataManager;
import io.jmix.core.security.Authenticated;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class HomeAssistantJob implements Job {

    @Autowired
    private DataManager dataManager;


//    public ModbusJob() {
//        if(config != null)
//            service.setConfig(config);
//    }

    @Authenticated
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        queryValues();
    }

    /**
     *
     */
    private void queryValues() {
        List<HomeAssistantStates> registersIO = dataManager.load(HomeAssistantStates.class).all().list();

        registersIO.forEach(homeAssistantRegister -> {
            // Set Value to Cache.
            CacheHandler.setValue("homeassistant." + homeAssistantRegister.getName(), HAHandler.getSensorState(homeAssistantRegister.getName()).get("state").getAsString());
        });
    }


}
