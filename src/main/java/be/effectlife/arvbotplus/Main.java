package be.effectlife.arvbotplus;

import be.effectlife.arvbotplus.loading.*;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    private final StageBuilder stageBuilder = new StageBuilder();
    AESceneLoader sceneLoader = AESceneLoader.getInstance();
    private int preparedStageCount = 0;

    //s.getStylesheets().add(Main.class.getResource("bootstrap3.css").toExternalForm());
    @Override
    public void start(Stage primaryStage) throws Exception {

        prepareStages();
        primaryStage = StageBuilder.getStage(Stages.POLL);
        primaryStage.show();
        sceneLoader.getController(Scenes.S_POLL).add(sceneLoader.getScene(Scenes.W_POLL));

    }

    private void prepareStages() {
        buildStage(Stages.POLL, Scenes.S_POLL, false, CloseHandlers.SHUTDOWN).setOnShowing((e) -> {
            AESceneLoader.getInstance().getController(Scenes.S_POLL).onShow();
        });

        buildStage(Stages.INVENTORY, Scenes.S_INVENTORY, false, CloseHandlers.SHUTDOWN).setOnShowing((e) -> {

        });
        LOG.info("Prepared {} stages", preparedStageCount);
    }

    private Stage buildStage(Stages stage, Scenes scene, boolean ignoreSize, EventHandler<WindowEvent> closeHandler) {
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
