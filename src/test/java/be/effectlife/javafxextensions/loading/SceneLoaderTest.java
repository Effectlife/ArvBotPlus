package be.effectlife.javafxextensions.loading;

import be.effectlife.AbstractJavaFxTest;
import be.effectlife.MemoryAppender;
import be.effectlife.arvbotplus.ArvBotScenes;
import be.effectlife.testhelpers.TestScenes;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import javafx.scene.Scene;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class SceneLoaderTest extends AbstractJavaFxTest {
    private SceneLoader sceneLoader;
    private MemoryAppender memoryAppender;

    @Before
    public void setUp() {
        Logger logger = (Logger) LoggerFactory.getLogger(SceneLoader.class);
        memoryAppender = new MemoryAppender();
        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logger.setLevel(Level.ALL);
        logger.addAppender(memoryAppender);
        memoryAppender.start();
        sceneLoader = SceneLoader.getInstance();

    }

    @Test
    public void getScene_loadsAndReturnsSceneThenReturnsAlreadyLoadedScene() {
        Scene scene = sceneLoader.getScene(ArvBotScenes.S_POLL);
        Scene scene2 = sceneLoader.getScene(ArvBotScenes.S_POLL);
        assertNotNull(scene);
        assertNotNull(scene2);
        assertEquals(scene, scene2);
        assertTrue(memoryAppender.contains("Initializing s_poll", Level.INFO));

    }

    @Test
    public void getScene_withAddonLoadsAndReturnsDifferentScenes() {
        Scene scene = sceneLoader.getScene(ArvBotScenes.S_POLL, "1");
        Scene scene2 = sceneLoader.getScene(ArvBotScenes.S_POLL, "2");
        assertNotNull(scene);
        assertNotNull(scene2);
        assertNotEquals(scene, scene2);
    }

    @Test
    public void getScene_notExistingThrowsSceneNotFoundException() {
        sceneLoader.getScene(TestScenes.TESTNOTFOUND);
        assertTrue(memoryAppender.contains("Tried to load a Scene that does not exist", Level.ERROR));
    }
}