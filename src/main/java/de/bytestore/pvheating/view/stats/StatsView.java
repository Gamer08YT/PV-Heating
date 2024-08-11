package de.bytestore.pvheating.view.stats;


import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.entity.StatsItem;
import de.bytestore.pvheating.handler.CacheHandler;
import de.bytestore.pvheating.service.StatsService;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.chartsflowui.component.Chart;
import io.jmix.chartsflowui.data.item.SimpleDataItem;
import io.jmix.chartsflowui.kit.component.model.DataSet;
import io.jmix.chartsflowui.kit.data.chart.ListChartItems;
import io.jmix.core.DataManager;
import io.jmix.flowui.component.combobox.JmixComboBox;
import io.jmix.flowui.view.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

@Route(value = "stats-view", layout = MainView.class)
@ViewController("heater_StatsView")
@ViewDescriptor("stats-view.xml")
public class StatsView extends StandardView {
    // Store Items of Stats Graph.
    private ListChartItems<SimpleDataItem> itemsIO = new ListChartItems<>();

    @ViewComponent
    private Chart statsChart;
    @ViewComponent
    private JmixComboBox<String> statsEntity;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private StatsService statsService;

    @Subscribe
    public void onInit(final InitEvent event) {
        statsEntity.setItems(CacheHandler.getCache().keySet());

        statsChart.withDataSet(new DataSet().withSource(new DataSet.Source<SimpleDataItem>().withDataProvider(itemsIO).withCategoryField("createdDate").withValueField("value")));
    }

    @Subscribe("statsEntity")
    public void onStatsEntityComponentValueChange(final AbstractField.ComponentValueChangeEvent<JmixComboBox<?>, ?> event) {
        // Remove all old Stats Items.
        itemsIO.removeAll();

        // Querry new Stats Items.
        List<StatsItem> statsList = statsService.getByType(statsEntity.getValue());

        // Add new Stats Items.
        if(statsList != null) {
            statsList.forEach(stats -> {
                if(stats != null && stats.getValue() != null && stats.getCreatedDate() != null)
                    itemsIO.addItem(new SimpleDataItem(new StatsChartItem(Float.valueOf(stats.getValue()), stats.getCreatedDate())));
            });
        }
    }

    @Data
    @AllArgsConstructor
    public class StatsChartItem {
        private Float value;

        private LocalDateTime createdDate;
    }

}

