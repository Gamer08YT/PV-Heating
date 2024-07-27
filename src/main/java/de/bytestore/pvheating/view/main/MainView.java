package de.bytestore.pvheating.view.main;

import com.pi4j.boardinfo.definition.PiModel;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.handler.CacheHandler;
import de.bytestore.pvheating.handler.HAHandler;
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

    // Create new Decimal Formatter Instance.
    private static DecimalFormat formatIO = new DecimalFormat("0.00");

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
            currentPower.setText((Double) CacheHandler.getValue("current-power") + " W");
            flowRate.setText(formatIO.format((Double) CacheHandler.getValue("flow-per-minute")) + " l/min");
        }));
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
