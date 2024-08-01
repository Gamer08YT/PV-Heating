package de.bytestore.pvheating.view.main;

import com.pi4j.boardinfo.definition.PiModel;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.handler.CacheHandler;
import de.bytestore.pvheating.service.ModbusService;
import de.bytestore.pvheating.service.Pi4JService;
import io.jmix.core.Messages;
import io.jmix.flowui.app.main.StandardMainView;
import io.jmix.flowui.component.main.JmixListMenu;
import io.jmix.flowui.view.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.text.DecimalFormat;
import java.time.Duration;

@Route("")
@ViewController("heater_MainView")
@ViewDescriptor("main-view.xml")
public class MainView extends StandardMainView {
    private static final Logger log = LoggerFactory.getLogger(MainView.class);
    @ViewComponent
    private Span nonSupportedBoard;
    @ViewComponent
    private JmixListMenu menu;
    @ViewComponent
    private Span useSupportedBoard;
    @Autowired
    private MessageBundle messageBundle;
    @Autowired
    private Messages messages;

    @Autowired
    private Pi4JService service;
    @ViewComponent
    private Span currentPower;

    // Store Disposable for Updating UI.
    private Disposable subscription;
    @ViewComponent
    private Span flowRate;

    @Autowired
    private ModbusService modbusService;

    // Create new Decimal Formatter Instance.
    private static DecimalFormat formatIO = new DecimalFormat("0.00");
    @ViewComponent
    private Span currentTemperature;
    @ViewComponent
    private Span usablePower;
    @ViewComponent
    private VerticalLayout maxPowerCard;
    @ViewComponent
    private Span modbusMaxAttempts;

    @Subscribe
    public void onReady(final ReadyEvent event) {
        if(service.getModel() == null || service.getModel().getModel() == PiModel.UNKNOWN) {
            // Show Error Message.
            nonSupportedBoard.setVisible(true);

            // Disable GPIO Element.

        } else {
            useSupportedBoard.setText(messageBundle.formatMessage("hasSupportedBoard", service.getModel().getLabel()));
            useSupportedBoard.setVisible(true);
        }
        
    }

    @Subscribe
    public void onInit(final InitEvent event) {
        this.refreshStats();
    }

    /**
     * Refreshes the statistics displayed on the screen.
     */
    private void refreshStats() {
        getUI().ifPresent(ui -> ui.access(() -> {
            currentPower.setText(formatIO.format((Double) CacheHandler.getValueOrDefault("current-power", 0.00)) + " W");
            flowRate.setText(formatIO.format((Double) CacheHandler.getValueOrDefault("flow-per-minute", 0.00)) + " l/min");
            currentTemperature.setText(formatIO.format((Double) CacheHandler.getValueOrDefault("temperature", 0.00)) + " °C");

            // Set Card Color (Working State).
            setWorkingState((Double) CacheHandler.getValueOrDefault("usable-power", 0.00));

            // Set Modbus Error State.
            modbusMaxAttempts.setText(messageBundle.formatMessage("modbusMaxAttempts", modbusService.getPort()));
            modbusMaxAttempts.setVisible(modbusService.getFails() <= 3);
        }));
    }

    /**
     * Sets the working state based on the provided value.
     *
     * @param valueIO The value to determine the working state. A positive value indicates online state, while 0 indicates offline state.
     */
    private void setWorkingState(Double valueIO) {
        maxPowerCard.setClassName("online", (valueIO > 0));
        maxPowerCard.setClassName("offline", (valueIO == 0 || valueIO < 0));

        if(valueIO > 0)
            usablePower.setText(formatIO.format(valueIO) + " W");
        else
            usablePower.setText(formatIO.format(0) + " W");

            setSCRTooltip();
    }

    /**
     * Sets the SCR (Silicon Controlled Rectifier) tooltip for the usablePower component.
     *
     * The method retrieves values from a cache using the CacheHandler class for three different keys: "scr-pwm", "scr-voltage", and "scr-current".
     * If a value is found for a key, a tooltip is set for the usablePower component, displaying the corresponding value followed by the appropriate unit (Hz, V, mA).
     *
     * This method does not return a value.
     *
     * @see CacheHandler#getValue(String)
     * @see Tooltip#withText(String)
     *
     * @since (version number or release date)
     */
    private void setSCRTooltip() {
        // Set PWM Tooltip if available.
        Object pwmIO = (double) CacheHandler.getValue("scr-pwm");

        if(pwmIO != null) {
            Tooltip.forComponent(usablePower).withText(pwmIO + " Hz");
        }

        // Set Voltage Tooltip if available.
        Object voltageIO = (double) CacheHandler.getValue("scr-voltage");

        if(voltageIO != null) {
            Tooltip.forComponent(usablePower).withText(voltageIO + " V");
        }

        // Set Voltage Tooltip if available.
        Object currentIO = (double) CacheHandler.getValue("scr-current");

        if(currentIO != null) {
            Tooltip.forComponent(usablePower).withText(currentIO + " mA");
        }
    }

    /**
     * Subscribes to an AttachEvent and performs certain actions.
     * This method is invoked when an AttachEvent is fired.
     *
     * @param event The AttachEvent object representing the event.
     */
    @Subscribe
    public void onAttachEvent(final AttachEvent event) {
        subscription = Flux.interval(Duration.ofSeconds(1))
                .publishOn(Schedulers.parallel())
                .subscribe(e -> {
                    this.refreshStats();
                });
    }

    /**
     * Subscribes to a DetachEvent and performs certain actions. This method is invoked when a DetachEvent is fired.
     *
     * @param event The DetachEvent object representing the event.
     */
    @Subscribe
    public void onDetachEvent(final DetachEvent event) {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }


}
