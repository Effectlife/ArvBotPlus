package be.effectlife.arvbotplus.controllers.scenes;

import be.effectlife.arvbotplus.controllers.IController;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.text.Text;

public class GivawayController implements IController {
    private static final Logger LOG = LoggerFactory.getLogger(GivawayController.class);

    @FXML
    private TextField tfGivawayWord;

    @FXML
    private Button btnStartStop;

    @FXML
    private Button btnLastCallRedraw;

    @FXML
    private Text textTotalEntries;

    @FXML
    private Text textTimeLeft;

    @FXML
    private Text textChance;

    @FXML
    private Text textGivawayStatus;

    @FXML
    private Text textWinnerUsername;

    @FXML
    private CheckBox cbWrapArcanusbot;

    @FXML
    private CheckBox cbCountdown;

    @FXML
    private Spinner<?> spinnerCountdown;

    @FXML
    private CheckBox cbBlink;

    @FXML
    void btnLastCallRedraw(ActionEvent event) {

    }

    @FXML
    void btnStartStop(ActionEvent event) {

    }

    @Override
    public void doInit() {

    }

    @Override
    public void reloadView() {

    }
}
