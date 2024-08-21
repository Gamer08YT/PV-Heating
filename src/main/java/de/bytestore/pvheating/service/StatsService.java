package de.bytestore.pvheating.service;

import de.bytestore.pvheating.entity.Stats;
import de.bytestore.pvheating.entity.StatsItem;
import io.jmix.core.DataManager;
import io.jmix.core.NoResultException;
import io.jmix.core.querycondition.PropertyCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StatsService {
    private final DataManager dataManager;

    public StatsService(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    /**
     * Publishes a value associated with a key to the Stats entity.
     *
     * @param keyIO the key with which the value is associated
     * @param valueIO the value to be published
     */
    public void publish(String keyIO, Object valueIO) {
        Stats statsIO;

        try {
            statsIO = getParent(keyIO);
        } catch (NoResultException e) {
            statsIO = dataManager.create(Stats.class);
            statsIO.setType(keyIO);
            dataManager.save(statsIO);

            log.info("Created new Stats Entity Entry for {}.", keyIO);
        }

        StatsItem itemIO = dataManager.create(StatsItem.class);
        itemIO.setValue(valueIO.toString());
        itemIO.setStats(statsIO);

        dataManager.save(itemIO);
    }

    /**
     * Retrieves the parent Stats entity for a given key.
     *
     **/
    private Stats getParent(String keyIO) {
       return dataManager.load(Stats.class).condition(PropertyCondition.equal("type", keyIO)).one();
    }

    public List<StatsItem> getByType(String keyIO) {
        return this.getParent(keyIO).getValues();
    }

    /**
     * Retrieves all Stats entities from the data manager.
     *
     * @return ArrayList of Stats objects containing all the retrieved entities.
     */
    public List<Stats> getAll() {
        return (List<Stats>) dataManager.load(Stats.class).all().list();
    }
}
