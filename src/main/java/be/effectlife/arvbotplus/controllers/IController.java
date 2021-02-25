package be.effectlife.arvbotplus.controllers;

import be.effectlife.arvbotplus.loading.SceneContainer;
import javafx.scene.Scene;
import org.slf4j.LoggerFactory;

public interface IController {
    default void add(Scene sceneContainer){
        LoggerFactory.getLogger(IController.class).warn("Calling add on ");
    }

    void doInit();

    void onShow();
}