package de.bytestore.pvheating.jobs;

import de.bytestore.pvheating.entity.Stats;
import de.bytestore.pvheating.handler.CacheHandler;
import de.bytestore.pvheating.handler.HAHandler;
import io.jmix.core.DataManager;
import io.jmix.core.querycondition.LogicalCondition;
import io.jmix.core.querycondition.PropertyCondition;
import io.jmix.core.security.Authenticated;
import jakarta.persistence.EntityManager;
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

//    @Autowired
//    private EntityManager entityManager;

    @Authenticated
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        this.addCache();
        this.removeOld();
    }

    /**
     * Adds the cache values to the database.
     */
    private void addCache() {
        if(CacheHandler.getCache() != null) {
            CacheHandler.getCache().forEach((keyIO, valueIO) -> {
                Stats statsIO = manager.create(Stats.class);
                statsIO.setType(keyIO);
                statsIO.setValue(valueIO.toString());

                manager.save(statsIO);
            });
        }
    }

    /**
     * Removes old heater stats records from the database.
     */
    private void removeOld() {
        OffsetDateTime dateTime = OffsetDateTime.now().minusDays(30);

        manager.load(Stats.class).condition(PropertyCondition.less("createdDate", dateTime));
    }

}
