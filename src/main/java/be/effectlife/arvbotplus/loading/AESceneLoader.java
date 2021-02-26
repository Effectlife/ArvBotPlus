package be.effectlife.arvbotplus.loading;

import be.effectlife.arvbotplus.controllers.IController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AESceneLoader {
    private static final Map<String, SceneContainer> scenes = new HashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(AESceneLoader.class);

    private static AESceneLoader instance;

    public static AESceneLoader getInstance() {
        if (instance == null) {
            instance = new AESceneLoader();
        }
        return instance;
    }


    public Scene getScene(Scenes scene) {
        return getScene(scene, "");
    }

    public Scene getScene(Scenes scene, String suffix) {
        if (scenes.containsKey(scene.toString() + suffix)) {
            return scenes.get(scene.toString() + suffix).getScene();
        }
        try {
            return loadScene(scene.toString(), suffix).getScene();
        } catch (SceneNotFoundException e) {
            LOG.error("Tried to load a Scene that does not exist", e);
        }
        return null;
    }

    public IController getController(Scenes scene) {
        return getController(scene, "");
    }

    public IController getController(Scenes scene, String suffix) {
        if (scenes.containsKey(scene.toString() + suffix)) {
            return scenes.get(scene.toString() + suffix).getController();
        }
        try {
            return loadScene(scene.toString(), suffix).getController();
        } catch (SceneNotFoundException e) {
            LOG.error("Tried to load a Controller that does not exist", e);
        }
        return null;
    }

    public SceneContainer getSceneContainer(Scenes scene) {
        return getSceneContainer(scene, "");
    }

    public SceneContainer getSceneContainer(Scenes scene, String suffix) {
        if (scenes.containsKey(scene.toString() + suffix)) {
            return scenes.get(scene.toString() + suffix);
        }
        try {
            return loadScene(scene.toString(), suffix);
        } catch (SceneNotFoundException e) {
            LOG.error("Tried to load a Controller that does not exist", e);
        }
        return null;
    }


    private SceneContainer loadScene(String sceneName, String addon) throws SceneNotFoundException {
        String fileName = sceneName;
        if (!fileName.endsWith(".fxml")) fileName += ".fxml"; //Adds .fxml extension if it is not present
        URL url = AESceneLoader.class.getClassLoader().getResource("scenes/" + fileName);
//        URL url = AESceneLoader.class.getClassLoader().getResource("Scenes/" + fileName);

        if (url == null) {
            throw new SceneNotFoundException("File Scenes/" + fileName + " does not exist");
        }

        FXMLLoader loader = new FXMLLoader(url);
        SceneContainer container = null;
        try {
            Scene s = new Scene(loader.load());
            s.getStylesheets().add(AESceneLoader.class.getResource("/css/bootstrap3-dark.css").toExternalForm());
            container = new SceneContainer(s, loader.getController());
            scenes.put(sceneName + addon, container);
        } catch (IOException e) {
            throw new SceneNotFoundException(e);
        }
        LOG.info("Initializing "+container.getController().getClass().getSimpleName());
        container.getController().doInit();
        return container;
    }

}
