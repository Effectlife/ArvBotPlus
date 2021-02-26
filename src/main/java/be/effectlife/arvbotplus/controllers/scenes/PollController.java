package be.effectlife.arvbotplus.controllers.scenes;

import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.loading.SceneContainer;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PollController implements IController {
private static Logger LOG = LoggerFactory.getLogger(PollController.class);
    @FXML
    private GridPane base;
    public void add(Scene sceneContainer){
    }

    @Override
    public void doInit() {
    }

    @Override
    public void onShow() {

    }
}
