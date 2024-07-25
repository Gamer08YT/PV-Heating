package de.bytestore.pvheating.view.main;

import com.pi4j.boardinfo.definition.PiModel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import de.bytestore.pvheating.handler.GPIOHandler;
import io.jmix.core.Messages;
import io.jmix.flowui.app.main.StandardMainView;
import io.jmix.flowui.component.main.JmixListMenu;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
@ViewController("heater_MainView")
@ViewDescriptor("main-view.xml")
public class MainView extends StandardMainView {
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

    @Subscribe
    public void onReady(final ReadyEvent event) {
        if(GPIOHandler.modelIO == null || GPIOHandler.modelIO.getModel().equals(PiModel.UNKNOWN)) {
            // Show Error Message.
            nonSupportedBoard.setVisible(true);

            // Disable GPIO Element.

        } else {
            useSupportedBoard.setText(messageBundle.formatMessage("hasSupportedBoard", GPIOHandler.modelIO.getLabel()));
            useSupportedBoard.setVisible(true);
        }
    }

}
