package be.effectlife.arvbotplus;

import be.effectlife.arvbotplus.loading.*;
import be.effectlife.arvbotplus.twirk.TwirkSystem;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main extends Application {

    public static final String PREFIX = "&";
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    private final StageBuilder stageBuilder = new StageBuilder();
    private int preparedStageCount = 0;
    public static TwirkSystem twirkSystem;

    @Override
    public void start(Stage primaryStage) throws Exception {


        primaryStage.setScene(AESceneLoader.getInstance().getScene(Scenes.S_LOADING));
        primaryStage.show();
        prepareStages();
        Stage loadingStage = primaryStage;
        primaryStage = StageBuilder.getStage(Stages.POLL);
        Stage finalPrimaryStage = primaryStage;
        new Thread(() -> {
            twirkSystem = new TwirkSystem();
            try {
                twirkSystem.initializeSystem(false);
                Platform.runLater(() -> {
                    loadingStage.hide();
                    finalPrimaryStage.show();
                });
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        //primaryStage.show();
    }

    private void prepareStages() {
        buildStage(Stages.POLL, Scenes.S_POLL, CloseHandlers.SHUTDOWN).setOnShowing((e) -> {
            AESceneLoader.getInstance().getController(Scenes.S_POLL).onShow();
            ((Stage) e.getSource()).setMinWidth(Scenes.S_POLL.getMinWidth());
        });

        buildStage(Stages.INVENTORY, Scenes.S_INVENTORY, CloseHandlers.SHUTDOWN).setOnShowing((e) -> {
        });
        LOG.info("Prepared {} stages", preparedStageCount);
    }

    private Stage buildStage(Stages stage, Scenes scene, EventHandler<WindowEvent> closeHandler) {
        preparedStageCount++;
        return stageBuilder.setStageIdentifier(stage)
                .setSceneIdentifier(scene)
                .setTitle(scene.getTitle())
                .setOnCloseHandler(closeHandler)
                .build();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
