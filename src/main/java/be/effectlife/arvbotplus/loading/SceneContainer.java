package be.effectlife.arvbotplus.loading;

import be.effectlife.arvbotplus.controllers.IController;
import javafx.scene.Scene;

public class SceneContainer {
    private final IController controller;
    private final Scene scene;

    public SceneContainer(Scene scene, IController controller) {
        this.controller = controller;
        this.scene = scene;
    }

    public IController getController() {
        return controller;
    }

    public Scene getScene() {
        return scene;
    }
}
