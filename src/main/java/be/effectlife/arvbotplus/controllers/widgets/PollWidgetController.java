package be.effectlife.arvbotplus.controllers.widgets;

import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.controllers.scenes.PollController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.SceneContainer;
import be.effectlife.arvbotplus.loading.Scenes;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;

public class PollWidgetController implements IController {
    @FXML
    private ProgressBar pBarVotes;

    @FXML
    private Spinner<Integer> spinnerVotes;

    @FXML
    private Button btnClear;

    @FXML
    private TextArea taOption;
    private PollController pollController;

    @FXML
    void btnClear_Clicked(ActionEvent event) {
        spinnerVotes.getValueFactory().setValue(0);
        pBarVotes.setProgress(0);
        taOption.setText("");
    }


    @Override
    public void doInit() {
        pollController = (PollController) AESceneLoader.getInstance().getController(Scenes.S_POLL);
        spinnerVotes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
        btnClear_Clicked(null);
    }

    @Override
    public void onShow() {

    }

    @Override
    public void reloadView() {

    }

    public void clear() {
        btnClear_Clicked(null);
    }
}
