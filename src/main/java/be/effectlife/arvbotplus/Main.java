package be.effectlife.arvbotplus;

import be.effectlife.arvbotplus.loading.*;
import be.effectlife.arvbotplus.twirk.TwirkSystem;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    private final StageBuilder stageBuilder = new StageBuilder();
    private int preparedStageCount = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        prepareStages();
        TwirkSystem twirkSystem = new TwirkSystem();
        twirkSystem.initializeSystem(true);
        primaryStage = StageBuilder.getStage(Stages.POLL);
        primaryStage.show();
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
