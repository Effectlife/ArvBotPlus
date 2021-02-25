package be.effectlife.arvbotplus.utilities;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public abstract class SimplePopup {

    private static Alert prepareAlert(String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }

    public static ConfirmationType showPopupYesNoCancel(String title, String header, String content) {
        Alert alert = prepareAlert(title, header, content);

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
        Alert alert = prepareAlert(title, header, content);

        ButtonType buttonYes = new ButtonType("Yes");
        ButtonType buttonNo = new ButtonType("No");
        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonYes) {
                return ConfirmationType.YES;
        }
        return ConfirmationType.NO;
    }
}
