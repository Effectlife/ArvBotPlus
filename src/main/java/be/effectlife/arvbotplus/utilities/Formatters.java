package be.effectlife.arvbotplus.utilities;

import javafx.scene.control.TextFormatter;

import java.text.DecimalFormat;
import java.text.ParsePosition;

public class Formatters {
    private Formatters() {
    }

    public static TextFormatter<?> getNumbersOnlyFormatter() {
        return new TextFormatter<>((c) -> {
            if (c.getControlNewText().isEmpty()) {
                return c;
            } else {
                ParsePosition parsePosition = new ParsePosition(0);
                Object object = (new DecimalFormat("#.0")).parse(c.getControlNewText(), parsePosition);
                return object != null && parsePosition.getIndex() >= c.getControlNewText().length() ? c : null;
            }
        });
    }
}
