package de.bytestore.pvheating.jobs;

import de.bytestore.pvheating.entity.StatsItem;
import de.bytestore.pvheating.handler.CacheHandler;
import de.bytestore.pvheating.handler.CalcHandler;
import de.bytestore.pvheating.service.StatsService;
import io.jmix.core.DataManager;
import io.jmix.core.querycondition.PropertyCondition;
import io.jmix.core.security.Authenticated;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;

@Slf4j
public class StatsJob implements Job {

    @Autowired
    private DataManager manager;

    @Autowired
    private StatsService statsService;

//    @Autowired
//    private EntityManager entityManager;

    @Authenticated
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        this.calc();
        this.addCache();
        this.removeOld();
    }


    private void calc() {
        CacheHandler.setValue("heating.time.needed", CalcHandler.calcTime((Double) CacheHandler.getValueOrDefault("temperature", 0.00), (Double) CacheHandler.getValueOrDefault("heater-power", 0.00)));
    }

    /**
     * Adds the cache values to the database.
     */
    private void addCache() {
        try {
            if (CacheHandler.getCache() != null) {
                CacheHandler.getCache().forEach((keyIO, valueIO) -> {
                    if (!keyIO.startsWith("dev") && !keyIO.startsWith("homeassistant") && !keyIO.startsWith("modbus") && !keyIO.startsWith("gpio") && !keyIO.startsWith("1wire"))
                        if (keyIO != null && valueIO != null)
                            statsService.publish(keyIO, valueIO);
                });
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Removes old heater stats records from the database.
     */
    private void removeOld() {
        OffsetDateTime dateTime = OffsetDateTime.now().minusDays(30);

        manager.load(StatsItem.class).condition(PropertyCondition.less("createdDate", dateTime));
    }

}
