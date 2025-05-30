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
import de.bytestore.pvheating.configuration.DefaultPinout;
import de.bytestore.pvheating.entity.ModbusSlave;
import de.bytestore.pvheating.handler.CacheHandler;
import de.bytestore.pvheating.handler.ConfigHandler;
import de.bytestore.pvheating.jobs.SCRJob;
import de.bytestore.pvheating.service.ModbusService;
import de.bytestore.pvheating.service.Pi4JService;
import de.bytestore.pvheating.service.StatsService;
import de.bytestore.pvheating.view.main.MainView;
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.backgroundtask.BackgroundTask;
import io.jmix.flowui.backgroundtask.BackgroundWorker;
import io.jmix.flowui.backgroundtask.TaskLifeCycle;
import io.jmix.flowui.component.checkbox.JmixCheckbox;
import io.jmix.flowui.component.textfield.JmixNumberField;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private ModbusService modbusService;

    @Autowired
    private Pi4JService pi4JService;
    @ViewComponent
    private ProgressBar calibrationProgress;
    @ViewComponent
    private Span currentPower;
    @ViewComponent
    private Span currentPWM;
    @Autowired
    private BackgroundWorker backgroundWorker;
    @Autowired
    private Dialogs dialogs;
    @Autowired
    private Notifications notifications;
    @ViewComponent
    private Span factor;
    @ViewComponent
    private JmixCheckbox enableStatus;
    @ViewComponent
    private JmixCheckbox enableError;
    @ViewComponent
    private JmixCheckbox enablePump;
    @ViewComponent
    private JmixCheckbox enableSCR;


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
        backgroundWorker.handle(new BackgroundTask<Object, Object>(TimeUnit.MINUTES.toSeconds(2)) {
            @Override
            public Object run(TaskLifeCycle<Object> taskLifeCycle) throws Exception {
                calibrate();

                return null;
            }
        }).execute();

    }

    /**
     * This method is used to calibrate the system.
     * It sets the "devCalibration" cache value to true.
     * It logs the start of the calibration process.
     * <p>
     * It gets the maximum PWM frequency and PWM step from the configuration.
     * It also gets the pause duration for stabilization.
     * <p>
     * It initializes arrays to store the PWM values and power values.
     * <p>
     * It sets the maximum value of the calibration progress and initializes it to 0.
     * <p>
     * It iterates from 0 to the maximum PWM frequency with the specified PWM step.
     * In each iteration, it sets the PWM frequency using the pi4JService.
     * It waits for a short duration to let the system stabilize.
     * It retrieves the power value using the getPower() method.
     * It updates the user interface with the current power and PWM frequency values.
     * It saves the power value in the cache.
     * It saves the PWM and power values in the corresponding arrays.
     * It updates the calibration progress.
     * <p>
     * After the iteration is complete, it calculates the calibration factor using the stored PWM and power values.
     */
    public void calibrate() {
        try {
            CacheHandler.setValue("devCalibration", true);

            pi4JService.getPWM(13).frequency(Double.valueOf(ConfigHandler.getCached().getScr().getMaxPWM()).intValue());

            pi4JService.setPumpState(true);
            pi4JService.setSCRState(true);

            log.info("Starting Calibration Process.");

            double MAX_PWM_FREQUENCY = 100;//Double.valueOf(ConfigHandler.getCached().getScr().getMaxPWM()).intValue();
            double PWM_STEP = 0.5;
            int PAUSE_DURATION = 500;

            int arraySize = (int) (MAX_PWM_FREQUENCY / PWM_STEP) + 1;
            double[] pwmValues = new double[arraySize];
            double[] powerValues = new double[arraySize];

            calibrationProgress.setMax(MAX_PWM_FREQUENCY);
            calibrationProgress.setValue(0);

            int index = 0;

            for (double frequency = 0; frequency <= MAX_PWM_FREQUENCY; frequency += PWM_STEP) {
                log.debug("Current frequency: " + frequency + " / Max frequency: " + MAX_PWM_FREQUENCY);

                // Setze die PWM-Frequenz
                pi4JService.getPWM(13).on(frequency);

                // Warte kurz, um das System stabilisieren zu lassen
                try {
                    Thread.sleep(PAUSE_DURATION);
                } catch (InterruptedException e) {
                    log.error("Error", e);
                }

                // Dummy-Aufruf der Methode, die die Leistung misst
                Double power = getPower();

                // Set Span Text.
                double finalFrequency = frequency;

                getUI().ifPresent(ui -> ui.access(() -> {
                    currentPower.setText(power.intValue() + " W");
                    currentPWM.setText(finalFrequency + " %");
                }));

                CacheHandler.setValue("heater-power", power);

                // Werte speichern
                pwmValues[index] = frequency;
                powerValues[index] = power;

                index++;

                calibrationProgress.setValue(frequency);
            }
            log.info("Calibration Process finished.");

            // Shutdown SCR.
            pi4JService.setPWM(13, 0.00);

            pi4JService.setPumpState(false);
            pi4JService.setSCRState(false);

            // Calculate Calibration Factor.
            calculateCalibrationFactor(pwmValues, powerValues);
        } catch (Exception exceptionIO) {
            // Shutdown SCR.
            pi4JService.setPWM(13, 0.00);

            log.error("Calibration Error", exceptionIO);
        }
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


    /**
     * Calculates the calibration factor based on the given arrays of PWM values and power values.
     *
     * @param pwmValues   the array of PWM values
     * @param powerValues the array of power values
     * @throws NullPointerException if pwmValues or powerValues is null
     */
    private void calculateCalibrationFactor(double[] pwmValues, double[] powerValues) {
        log.info("Calculating Calibration Factor.");

        double totalPwm = 0;
        double totalPower = 0;

        for (int i = 0; i < pwmValues.length; i++) {
            totalPwm += pwmValues[i];
            totalPower += powerValues[i];
        }

        double calibrationFactor = totalPower / totalPwm;


        getUI().ifPresent(ui -> ui.access(() -> {
            factor.setText(calibrationFactor + " W/PWM");
        }));

        log.info("Calibration Factor (1 W = X PWM): " + calibrationFactor);
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

            enableStatus.setValue(pi4JService.getPWM(DefaultPinout.STATUS_BUTTON_PWM_GPIO).isOn());
            enableError.setValue(pi4JService.getPWM(DefaultPinout.FAULT_BUTTON_PWM_GPIO).isOn());
            enablePump.setValue(pi4JService.isPumpEnabled());
            enableSCR.setValue(pi4JService.isSCREnabled());
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
        CacheHandler.setValue("devCalibration", false);

        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }

        log.info("Set DevMode to false.");
    }

    @Subscribe(id = "resetPWMDuty", subject = "clickListener")
    public void onResetPWMDutyClick(final ClickEvent<JmixButton> event) {
        SCRJob.maxPWM = 0;

        dialogs.createMessageDialog().withHeader("Development").withText("PWM Duty Counter reset to 0.").open();

        log.info("PWM Duty Counter reset to 0.");
    }

    @Subscribe("enableSCR")
    public void onEnableSCRComponentValueChange(final AbstractField.ComponentValueChangeEvent<JmixCheckbox, ?> event) {
        if (event.isFromClient())
            pi4JService.setSCRState(((JmixCheckbox) event.getSource()).getValue());
    }


    @Subscribe("enableError")
    public void onEnableErrorComponentValueChange(final AbstractField.ComponentValueChangeEvent<JmixCheckbox, ?> event) {
        if (event.isFromClient())
            pi4JService.setErrorStatus(((JmixCheckbox) event.getSource()).getValue());
    }

    @Subscribe("enablePump")
    public void onEnablePumpComponentValueChange(final AbstractField.ComponentValueChangeEvent<JmixCheckbox, ?> event) {
        if (event.isFromClient())
            pi4JService.setPumpState(((JmixCheckbox) event.getSource()).getValue());
    }

    @Subscribe("enableStatus")
    public void onEnableStatusComponentValueChange(final AbstractField.ComponentValueChangeEvent<JmixCheckbox, ?> event) {
        if (event.isFromClient())
            pi4JService.setEnableStatus(((JmixCheckbox) event.getSource()).getValue());
    }


}