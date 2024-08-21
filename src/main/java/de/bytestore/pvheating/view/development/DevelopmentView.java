package de.bytestore.pvheating.view.development;


import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.html.RangeInput;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.handler.CacheHandler;
import de.bytestore.pvheating.service.StatsService;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.flowui.component.textfield.JmixNumberField;
import io.jmix.flowui.view.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@Slf4j
@Route(value = "development-view", layout = MainView.class)
@ViewController("heater_DevelopmentView")
@ViewDescriptor("development-view.xml")
public class DevelopmentView extends StandardView {

    @Autowired
    private StatsService statsService;

    @ViewComponent
    private VerticalLayout valuesBox;


    // Store Disposable for Updating UI.
    private Disposable subscription;
    @ViewComponent
    private RangeInput usablePower;
    @ViewComponent
    private JmixNumberField usablePowerInput;

    @Subscribe
    public void onInit(final InitEvent event) {

        // Print Info Message.
        log.info("Add Stats Items to UI.");

        // Add Stats Items.
        CacheHandler.getCache().forEach((s, o) -> {
            Span spanIO = new Span(s + ": " + o.toString());
            spanIO.setId("item-" + s);

            valuesBox.add(spanIO);
        });

        usablePower.setValue(0.0);
    }

    /**
     * Refreshes the statistics displayed in the user interface.
     * This method retrieves all the statistics from the {@link StatsService} and updates the corresponding UI components
     * with the latest values.
     * It iterates over the children components of the {@code valuesBox} and checks if the ID of the component matches
     * the ID constructed from the type of a statistic. If there is a match, the text of the component is set
     * to the latest value of the statistic.
     */
    private void refreshStats() {
        getUI().ifPresent(ui -> ui.access(() -> {
            CacheHandler.getCache().forEach((s, o) -> {
                valuesBox.getChildren().forEach(component -> {
                    Span spanIO = (Span) component;

                    if (spanIO.getId().get().equals("item-" + s)) {
                        spanIO.setText(s + ": " + o.toString());
                    }
                });
            });
        }));
    }

    @Subscribe("usablePower")
    public void onUsablePowerComponentValueChange(final AbstractField.ComponentValueChangeEvent<RangeInput, ?> event) {
        this.handlePowerUpdate((Double) event.getValue(), false);
    }

    @Subscribe("usablePowerInput")
    public void onUsablePowerInputComponentValueChange(final AbstractField.ComponentValueChangeEvent<JmixNumberField, ?> event) {
        this.handlePowerUpdate((Double) event.getValue(), true);
    }


    /**
     * Handles the power update with the given value and inputIO flag.
     * This method updates the usable power value and stores the current power value in the cache.
     *
     * @param value    the new power value
     * @param inputIO  a flag indicating whether the power update is from input or output
     */
    private void handlePowerUpdate(Double value, boolean inputIO) {
        log.info("Set Usable Power to {}.", value);

        CacheHandler.setValue("devPowerOverride", true);
        CacheHandler.setValue("current-power", value);

        if (inputIO)
            usablePower.setValue(value);
        else
            usablePowerInput.setValue(value);
    }

    /**
     * Subscribes to an AttachEvent and performs certain actions.
     * This method is invoked when an AttachEvent is fired.
     *
     * @param event The AttachEvent object representing the event.
     */
    @Subscribe
    public void onAttachEvent(final AttachEvent event) {
        CacheHandler.setValue("devMode", true);

        subscription = Flux.interval(Duration.ofSeconds(1))
                .publishOn(Schedulers.parallel())
                .subscribe(e -> {
                    this.refreshStats();
                });


        log.info("Set DevMode to true.");
    }

    /**
     * Subscribes to a DetachEvent and performs certain actions. This method is invoked when a DetachEvent is fired.
     *
     * @param event The DetachEvent object representing the event.
     */
    @Subscribe
    public void onDetachEvent(final DetachEvent event) {
        CacheHandler.setValue("devMode", false);
        CacheHandler.setValue("devPowerOverride", false);

        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }

        log.info("Set DevMode to false.");
    }


}