package be.effectlife.arvbotplus.loading;

import be.effectlife.arvbotplus.Main;
import be.effectlife.arvbotplus.utilities.ConfirmationType;
import be.effectlife.arvbotplus.utilities.SimplePopup;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CloseHandlers {
    private static Logger LOG = LoggerFactory.getLogger(CloseHandlers.class);

    private CloseHandlers() {
    }

    public static final EventHandler<WindowEvent> SHUTDOWN = t -> {

        ConfirmationType result = SimplePopup.showPopupYesNo("Close?", "Close?", "Are you sure you want to close the application?");
        switch (result) {
            case YES:
                if (Main.twirkSystem != null)
                    Main.twirkSystem.disconnect();
                Platform.exit();
                System.exit(0);
                break;
            case NO:
                t.consume();
                break;
            default:
                LOG.error("Illegal Confirmationtype was given: {}", result.name());
        }
    };

    public static final EventHandler<WindowEvent> HIDE_ON_CLOSE = t -> {
        ((Stage) t.getSource()).hide();
    };
    public static final EventHandler<WindowEvent> HIDE_ON_CLOSE_AND_DISCONNECT_TWIRK = t -> {
        ((Stage) t.getSource()).hide();
        Main.twirkSystem.disconnect();
    };

    public static final EventHandler<WindowEvent> IGNORE_CLOSING = t -> {
    };

}
