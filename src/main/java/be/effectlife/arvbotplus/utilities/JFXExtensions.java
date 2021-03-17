package be.effectlife.arvbotplus.utilities;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class JFXExtensions {
    public static final EventHandler<KeyEvent> tabTraverse = event -> {
        KeyCode code = event.getCode();

        if (code == KeyCode.TAB && !event.isShiftDown() && !event.isControlDown()) {
            event.consume();
            Node node = (Node) event.getSource();
            KeyEvent newEvent
                    = new KeyEvent(event.getSource(),
                    event.getTarget(), event.getEventType(),
                    event.getCharacter(), event.getText(),
                    event.getCode(), event.isShiftDown(),
                    true, event.isAltDown(),
                    event.isMetaDown());

            node.fireEvent(newEvent);
        }
    };
    private static long lastTime;
    private static boolean isdblClicked = false;
    public static boolean isDoubleClick() {
        long diff;
        long currentTime = System.currentTimeMillis();
        if (lastTime != 0 && currentTime != 0) {
            diff = currentTime - lastTime;
            isdblClicked = diff <= 215;
        }
        lastTime = currentTime;
        return isdblClicked;
    }
}
