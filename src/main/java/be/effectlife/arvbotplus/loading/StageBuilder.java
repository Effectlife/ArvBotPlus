package be.effectlife.arvbotplus.loading;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

public class StageBuilder {
    private static final Map<Stages, Stage> stageMap;
    private static final ClassLoader classLoader = StageBuilder.class.getClassLoader();
    private static final Logger LOG = LoggerFactory.getLogger(StageBuilder.class);
    private static final AESceneLoader aeSceneLoader;

    static {
        aeSceneLoader = AESceneLoader.getInstance();
        stageMap = new EnumMap<>(Stages.class);

    }

    private final ArrayList<String> stylesheets;
    private Stages stageIdentifier;
    private Scenes sceneIdentifier;
    private String title;
    private EventHandler<WindowEvent> onCloseHandler;
    private int minWidth;

    public StageBuilder() {
        stylesheets = new ArrayList<>();
    }


    /**
     * gets a stage with a specific stageIdentifier, when no stage with the stageIdentifier exists, it creates a new one and saves it
     *
     * @param identifier the stage to return
     * @return the stage
     */
    public static Stage getStage(Stages identifier) {
        if (stageMap.containsKey(identifier)) {
            return stageMap.get(identifier);
        }

        Stage newStage = new Stage();
        stageMap.put(identifier, newStage);
        return newStage;
    }

    public Stage build() {
        LOG.info("Building Stage {}", this.stageIdentifier);
        Stage stage = getStage(this.stageIdentifier);
        Scene scene = aeSceneLoader.getScene(this.sceneIdentifier);

        if (scene == null) {
            LOG.error("Scene could not be found");
            return null;
        }

        for (String stylesheet : stylesheets) {
            try {
                scene.getStylesheets().add(classLoader.getResource(stylesheet).toExternalForm());
            } catch (NullPointerException e) {
                LOG.warn("Could not find stylesheet '{}'", stylesheet);
            }
        }
        stage.setMinWidth(minWidth);
        stage.setTitle(this.title);
        stage.setScene(scene);
        stage.setOnCloseRequest(this.onCloseHandler);
        return stage;

    }

    public StageBuilder setStageIdentifier(Stages stageIdentifier) {
        this.stageIdentifier = stageIdentifier;
        return this;
    }

    public StageBuilder setSceneIdentifier(Scenes sceneIdentifier) {
        this.sceneIdentifier = sceneIdentifier;
        return this;
    }

    public StageBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public StageBuilder setOnCloseHandler(EventHandler<WindowEvent> onCloseHandler) {
        this.onCloseHandler = onCloseHandler;
        return this;
    }

    public StageBuilder addStyleSheet(String stylesheet) {
        this.stylesheets.add(stylesheet);
        return this;
    }

    public StageBuilder setMinWidth(int minWidth) {
        this.minWidth = minWidth;
        return this;
    }
}
