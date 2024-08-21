package de.bytestore.pvheating.view.development;


import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.html.RangeInput;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.entity.ModbusSlave;
import de.bytestore.pvheating.handler.CacheHandler;
import de.bytestore.pvheating.handler.ConfigHandler;
import de.bytestore.pvheating.service.ModbusService;
import de.bytestore.pvheating.service.Pi4JService;
import de.bytestore.pvheating.service.StatsService;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.flowui.component.textfield.JmixNumberField;
import io.jmix.flowui.kit.component.button.JmixButton;
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
    private ModbusService modbusService;
    @Autowired
    private Pi4JService pi4JService;
    @ViewComponent
    private ProgressBar calibrationProgress;


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

    @Subscribe(id = "calibrationBtn", subject = "clickListener")
    public void onCalibrationBtnClick(final ClickEvent<JmixButton> event) {

    }

    public void calibrate() {
        int MAX_PWM_FREQUENCY = Double.valueOf(ConfigHandler.getCached().getScr().getMaxPWM()).intValue();
        int PWM_STEP = 100;
        int PAUSE_DURATION = 10;

        int[] pwmValues = new int[MAX_PWM_FREQUENCY / PWM_STEP];
        double[] powerValues = new double[MAX_PWM_FREQUENCY / PWM_STEP];

        calibrationProgress.setMax(MAX_PWM_FREQUENCY);
        calibrationProgress.setValue(0);

        int index = 0;

        for (int frequency = 0; frequency <= MAX_PWM_FREQUENCY; frequency += PWM_STEP) {
            // Setze die PWM-Frequenz
            pi4JService.setPWM(13, Double.valueOf(frequency));

            // Warte kurz, um das System stabilisieren zu lassen
            try {
                Thread.sleep(PAUSE_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Dummy-Aufruf der Methode, die die Leistung misst
            double power = getPower();

            CacheHandler.setValue("scr-power", power);

            // Werte speichern
            pwmValues[index] = frequency;
            powerValues[index] = power;

            index++;

            calibrationProgress.setValue(frequency);
        }

        // Berechne den Kalibrierungsfaktor
        calculateCalibrationFactor(pwmValues, powerValues);
    }

    /**
     * Calculates and returns the power value from a connected device.
     *
     * @return The power value as a double.
     */
    private double getPower() {
        ModbusSlave slaveIO = new ModbusSlave();
        slaveIO.setBaud(9600);
        slaveIO.setDataBits(8);
        slaveIO.setStopBits(1);
        slaveIO.setParity(0);
        slaveIO.setPort("/dev/ttyUSB0");

        return ((Float) modbusService.readInput(slaveIO, 1, 52, "")).doubleValue();
    }


    private void calculateCalibrationFactor(int[] pwmValues, double[] powerValues) {
        double totalPwm = 0;
        double totalPower = 0;

        for (int i = 0; i < pwmValues.length; i++) {
            totalPwm += pwmValues[i];
            totalPower += powerValues[i];
        }

        double calibrationFactor = totalPower / totalPwm;
        System.out.println("Kalibrierungsfaktor (1 W = X PWM): " + calibrationFactor);
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
     * @param value   the new power value
     * @param inputIO a flag indicating whether the power update is from input or output
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