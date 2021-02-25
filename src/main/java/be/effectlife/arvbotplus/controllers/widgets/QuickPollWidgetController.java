package be.effectlife.arvbotplus.controllers.widgets;

import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.loading.SceneContainer;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;

public class QuickPollWidgetController implements IController {

    @FXML
    private Button buttonQP;

    @FXML
    private ProgressBar pBarA;

    @FXML
    private ProgressBar pBarB;

    @FXML
    private Text textCountA;

    @FXML
    private Text textCountB;

    @Override
    public void add(Scene sceneContainer) {

    }

    @Override
    public void doInit() {

    }

    @Override
    public void onShow() {

    }
}
