package be.effectlife.arvbotplus.utilities;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public abstract class SimplePopup {
    private SimplePopup() {
    }

    private static Alert prepareAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }

    public static ConfirmationType showPopupYesNoCancel(String title, String header, String content) {
        Alert alert = prepareAlert(Alert.AlertType.CONFIRMATION, title, header, content);

        ButtonType buttonYes = new ButtonType("Yes");
        ButtonType buttonNo = new ButtonType("No");
        ButtonType buttonCancel = new ButtonType("Cancel");
        alert.getButtonTypes().setAll(buttonYes, buttonNo, buttonCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == buttonYes) {
                return ConfirmationType.YES;
            } else if (result.get() == buttonNo) {
                return ConfirmationType.NO;
            }
        }
        return ConfirmationType.CANCEL;
    }

    public static ConfirmationType showPopupYesNo(String title, String header, String content) {
        Alert alert = prepareAlert(Alert.AlertType.CONFIRMATION, title, header, content);

        ButtonType buttonYes = new ButtonType("Yes");
        ButtonType buttonNo = new ButtonType("No");
        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonYes) {
            return ConfirmationType.YES;
        }
        return ConfirmationType.NO;
    }

    public static void showPopupError(String content) {
        Alert alert = prepareAlert(Alert.AlertType.ERROR, "Oops", "Error", content);

        ButtonType buttonOK = new ButtonType("OK");
        alert.getButtonTypes().setAll(buttonOK);

        alert.showAndWait();
    }

    public static void showPopupInfo(String content) {
        showPopupInfo("Info", content);
    }

    public static void showPopupInfo(String header, String content) {
        Alert alert = prepareAlert(Alert.AlertType.INFORMATION, "Info", header, content);

        ButtonType buttonOK = new ButtonType("OK");
        alert.getButtonTypes().setAll(buttonOK);

        alert.showAndWait();
    }

    public static void showPopupWarn(String content) {
        Alert alert = prepareAlert(Alert.AlertType.WARNING, "Oops", "Warn", content);

        ButtonType buttonOK = new ButtonType("OK");
        alert.getButtonTypes().setAll(buttonOK);

        alert.showAndWait();
    }
}
