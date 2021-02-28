package be.effectlife.arvbotplus.controllers;

import javafx.scene.layout.Region;

public interface IController {

    void doInit();

    void onShow();

    void reloadView();

    default void setDefaultSize(Region region) {
        region.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        region.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }
}