package be.effectlife.arvbotplus.controllers.scenes;

import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.loading.SceneContainer;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;

public class PollController implements IController {

    @FXML
    private GridPane base;
    public void add(Scene sceneContainer){
        base.add(sceneContainer.getRoot(), 0,0);
        System.out.println(sceneContainer.getRoot());
    }

    @Override
    public void doInit() {

    }

    @Override
    public void onShow() {

    }
}
