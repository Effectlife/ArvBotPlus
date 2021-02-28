package be.effectlife.arvbotplus.controllers.widgets;

import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.loading.SceneContainer;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

public class PollWidgetController implements IController {
    @FXML
    private ProgressBar progressBar;

    @FXML
    private Spinner<Integer> spinner;

    @FXML
    private Button button;

    @FXML
    private TextArea textArea;

    @FXML
    private Text text;

    @Override
    public void doInit() {

    }

    @Override
    public void onShow() {

    }

    @Override
    public void reloadView() {

    }
}
