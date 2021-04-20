package be.effectlife.arvbotplus;

import be.effectlife.arvbotplus.controllers.scenes.DiceController;
import be.effectlife.arvbotplus.controllers.scenes.InventoryController;
import be.effectlife.arvbotplus.loading.*;
import be.effectlife.arvbotplus.saves.SaveManager;
import be.effectlife.arvbotplus.twirk.TwirkSystem;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Main extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    private final StageBuilder stageBuilder = new StageBuilder();
    private int preparedStageCount = 0;
    public static TwirkSystem twirkSystem;
    private static final Map<Stages, Stage> stageMap = new HashMap<>();

    @Override
    public void start(Stage primaryStage) throws Exception {

        prepareStages();
        Properties properties = loadProperties();
        InventoryController controller = (InventoryController) AESceneLoader.getInstance().getController(Scenes.S_INVENTORY);
        controller.setProperties(properties);
        controller.initializeSkillWidgets();
        stageMap.get(Stages.INVENTORY).show();
    }

    private Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        String propFileName = "./config.properties";
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(propFileName);
        } catch (FileNotFoundException e) {
            LOG.error("Cannot find properties file " + propFileName + "; Generating default file and exiting program. Please check twitch configuration");
            try {
                writePropertiesFile(propFileName);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            System.exit(1);
        }
        properties.load(inputStream);
        return properties;
    }

    private void writePropertiesFile(String propFileName) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(propFileName));
        bw.write("#Twitch integration settings. Insert name and token without < & >\n" +
                "twitch.channel=<insert channel name>\n" +
                "twitch.bot.oauthtoken=<insert oauth token>\n" +
                "\n" +
                "#Default skillset for the inventory system. Only name is required to create a widget. All other properties are optional in here\n" +
                "inv.skill.default.0.name=Health\n" +
                "inv.skill.default.0.value=5\n" +
                "inv.skill.default.0.maxValue=20\n" +
                "inv.skill.default.0.hasmaxvalue=true\n" +
                "inv.skill.default.0.usescolor=true\n" +
                "inv.skill.default.1.name=Mana\n" +
                "inv.skill.default.1.value=15\n" +
                "inv.skill.default.1.maxValue=20\n" +
                "inv.skill.default.1.hasmaxvalue=true\n" +
                "inv.skill.default.2.name=Gold\n" +
                "inv.skill.default.2.value=5\n" +
                "inv.skill.default.2.maxValue=20\n" +
                "inv.skill.default.2.hasmaxvalue=false\n" +
                "\n" +
                "#Threshold percentages to change color if value is below maxValue (warn = orange, crit = red)\n" +
                "inv.skill.threshold.warn=0.5\n" +
                "inv.skill.threshold.crit=0.2"
        );
        bw.flush();
        bw.close();
    }

    private void prepareStages() {
        Stage pollStage = buildStage(Stages.POLL, Scenes.S_POLL, CloseHandlers.HIDE_ON_CLOSE_AND_DISCONNECT_TWIRK);
        pollStage.setOnShowing((e) -> {
            AESceneLoader.getInstance().getController(Scenes.S_POLL).onShow();
            ((Stage) e.getSource()).setMinWidth(Scenes.S_POLL.getMinWidth());
        });
        stageMap.put(Stages.POLL, pollStage);
        Stage inventoryStage = buildStage(Stages.INVENTORY, Scenes.S_INVENTORY, CloseHandlers.SHUTDOWN);
        inventoryStage.setOnShowing((e) -> {
            AESceneLoader.getInstance().getController(Scenes.S_INVENTORY).onShow();
            ((Stage) e.getSource()).setMinWidth(Scenes.S_INVENTORY.getMinWidth());
        });
        stageMap.put(Stages.INVENTORY, inventoryStage);
        SaveManager.setInventoryStage(inventoryStage);
        Stage diceStage = buildStage(Stages.DICE, Scenes.S_DICE, CloseHandlers.HIDE_ON_CLOSE);
        diceStage.setOnShowing((e) -> {
            AESceneLoader.getInstance().getController(Scenes.S_DICE).onShow();
            ((Stage) e.getSource()).setMinWidth(Scenes.S_DICE.getMinWidth());
        });
        stageMap.put(Stages.DICE, diceStage);
        diceStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            DiceController diceController = (DiceController) AESceneLoader.getInstance().getController(Scenes.S_DICE);
            diceController.reloadWidth((double) newValue * .8);
        });
        Stage battleStage = buildStage(Stages.BATTLE, Scenes.S_BATTLE, CloseHandlers.HIDE_ON_CLOSE);
        battleStage.setOnShowing((e) -> {
            AESceneLoader.getInstance().getController(Scenes.S_BATTLE).onShow();
            ((Stage) e.getSource()).setMinWidth(Scenes.S_BATTLE.getMinWidth());
        });
        stageMap.put(Stages.BATTLE, battleStage);
        Stage conversionStage = buildStage(Stages.CONVERSION, Scenes.S_CONV, CloseHandlers.HIDE_ON_CLOSE);
        conversionStage.setOnShowing((e) -> {
            AESceneLoader.getInstance().getController(Scenes.S_CONV).onShow();
            ((Stage) e.getSource()).setMinWidth(Scenes.S_CONV.getMinWidth());
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

    public static Stage getStage(Stages stage) {
        return stageMap.get(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
