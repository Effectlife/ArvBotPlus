package be.effectlife.arvbotplus;

import be.effectlife.arvbotplus.controllers.scenes.DiceController;
import be.effectlife.arvbotplus.controllers.scenes.InventoryController;
import be.effectlife.arvbotplus.loading.*;
import be.effectlife.arvbotplus.saves.SaveManager;
import be.effectlife.arvbotplus.services.TwirkService;
import be.effectlife.arvbotplus.utilities.BasicUtilities;
import be.effectlife.arvbotplus.utilities.ColorHelper;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

public class Main extends Application {

    private static final String DEFAULT_CONFIG = "twitch.channel=<insert channel name>\n" +
            "twitch.bot.oauthtoken=2pt10rbn3eytqfi8s5gx0gthqpkv5h\n" +
            "twitch.disabled = false\n" +
            "twitch.giveaway.displayname = arcanusbot\n\n" +
            "twitch.connection.retryattempts = 3\n\n" +
            "abp.cssTemplate = dark\n\n" +
            "#Default skillset for the inventory system\n" +
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
            "inv.skill.default.2.hasmaxvalue=false\n\n" +
            "#Threshold percentages to change color if value is below maxValue (warn = orange, crit = red)\n" +
            "inv.skill.threshold.warn=0.5\n" +
            "inv.skill.threshold.crit=0.2";
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    private static final Map<Stages, Stage> stageMap = new EnumMap<>(Stages.class);
    private static TwirkService twirkSystem;
    private final StageBuilder stageBuilder = new StageBuilder();
    private int preparedStageCount = 0;

    public static Stage getStage(Stages stage) {
        return stageMap.get(stage);
    }

    public static void main(String[] args) {
        LOG.info("-------------------");
        LOG.info("Starting ArvBotPlus");
        launch(args);
    }

    public static TwirkService getTwirkService() {
        return twirkSystem;
    }

    public static void setTwirkSystem(TwirkService paramTwirkSystem) {
        twirkSystem = paramTwirkSystem;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Properties properties = BasicUtilities.loadProperties("./config.properties",DEFAULT_CONFIG);
        String cssTemplate = properties.getProperty("abp.cssTemplate");
        AESceneLoader.getInstance().setCssTemplate(cssTemplate);
        ColorHelper.setCssTemplate(cssTemplate);
        ColorHelper.init();
        prepareStages();
        InventoryController controller = (InventoryController) AESceneLoader.getInstance().getController(Scenes.S_INVENTORY);
        controller.setProperties(properties);
        controller.initializeSkillWidgets();
        stageMap.get(Stages.INVENTORY).show();
    }


    private void prepareStages() {
        Stage pollStage = buildStage(Stages.POLL, Scenes.S_POLL, CloseHandlers.HIDE_ON_CLOSE);
        pollStage.setOnShowing(e -> ((Stage) e.getSource()).setMinWidth(Scenes.S_POLL.getMinWidth()));
        Stage inventoryStage = buildStage(Stages.INVENTORY, Scenes.S_INVENTORY, CloseHandlers.SHUTDOWN);
        inventoryStage.setOnShowing(e -> ((Stage) e.getSource()).setMinWidth(Scenes.S_INVENTORY.getMinWidth()));
        SaveManager.setInventoryStage(inventoryStage);
        Stage diceStage = buildStage(Stages.DICE, Scenes.S_DICE, CloseHandlers.HIDE_ON_CLOSE);
        diceStage.setOnShowing(e -> ((Stage) e.getSource()).setMinWidth(Scenes.S_DICE.getMinWidth()));
        diceStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            DiceController diceController = (DiceController) AESceneLoader.getInstance().getController(Scenes.S_DICE);
            diceController.reloadWidth((double) newValue * .8);
        });
        Stage battleStage = buildStage(Stages.BATTLE, Scenes.S_BATTLE, CloseHandlers.HIDE_ON_CLOSE);
        battleStage.setOnShowing(e -> ((Stage) e.getSource()).setMinWidth(Scenes.S_BATTLE.getMinWidth()));
        Stage conversionStage = buildStage(Stages.CONVERSION, Scenes.S_CONV, CloseHandlers.HIDE_ON_CLOSE);
        conversionStage.setOnShowing(e -> ((Stage) e.getSource()).setMinWidth(Scenes.S_CONV.getMinWidth()));
        Stage questionStage = buildStage(Stages.QUESTIONS, Scenes.S_QUESTIONS, CloseHandlers.HIDE_ON_CLOSE);
        questionStage.setOnShowing(e -> ((Stage) e.getSource()).setMinWidth(Scenes.S_QUESTIONS.getMinWidth()));
        Stage giveawayStarge = buildStage(Stages.GIVEAWAYS, Scenes.S_GIVEAWAY, CloseHandlers.HIDE_ON_CLOSE);
        giveawayStarge.setOnShowing(e -> ((Stage) e.getSource()).setMinWidth(Scenes.S_GIVEAWAY.getMinWidth()));

        LOG.info("Prepared {} stages", preparedStageCount);
    }

    private Stage buildStage(Stages stage, Scenes scene, EventHandler<WindowEvent> closeHandler) {
        preparedStageCount++;
        Stage newStage = stageBuilder.setStageIdentifier(stage)
                .setSceneIdentifier(scene)
                .setTitle(scene.getTitle())
                .setOnCloseHandler(closeHandler)
                .build();
        stageMap.put(stage, newStage);
        return newStage;
    }
}
