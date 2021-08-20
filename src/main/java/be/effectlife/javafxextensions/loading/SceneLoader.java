package be.effectlife.javafxextensions.loading;

import be.effectlife.arvbotplus.controllers.IController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SceneLoader {
    private final Map<String, SceneContainer> scenes;
    private static final Logger LOG = LoggerFactory.getLogger(SceneLoader.class);

    private static SceneLoader instance;

    public static SceneLoader getInstance() {
        if (instance == null) {
            instance = new SceneLoader();
        }
        return instance;
    }

    private SceneLoader(){
        scenes = new HashMap<>();
    }

    public Scene getScene(Scenes scene) {
        return getScene(scene, "");
    }

    public Scene getScene(Scenes scene, String suffix) {
        if (scenes.containsKey(scene.toString() + suffix)) {
            return scenes.get(scene + suffix).getScene();
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
        URL url = SceneLoader.class.getClassLoader().getResource("scenes/" + fileName);
//        URL url = AESceneLoader.class.getClassLoader().getResource("Scenes/" + fileName);

        if (url == null) {
            throw new SceneNotFoundException("File scenes/" + fileName + " does not exist");
        }

        FXMLLoader loader = new FXMLLoader(url);
        SceneContainer container = null;
        try {
            Scene s = new Scene(loader.load());
            s.getStylesheets().add(SceneLoader.class.getResource("/css/bootstrap3-dark.css").toExternalForm());
            container = new SceneContainer(s, loader.getController());
            scenes.put(sceneName + addon, container);
        } catch (IOException e) {
            throw new SceneNotFoundException(e);
        }
        LOG.info("Initializing "+sceneName + addon);
        container.getController().doInit();
        return container;
    }

}
