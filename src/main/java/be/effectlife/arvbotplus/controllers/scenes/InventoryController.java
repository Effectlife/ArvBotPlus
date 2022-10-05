package be.effectlife.arvbotplus.controllers.scenes;

import be.effectlife.arvbotplus.Main;
import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.controllers.widgets.SkillWidgetController;
import be.effectlife.arvbotplus.loading.*;
import be.effectlife.arvbotplus.saves.SaveManager;
import be.effectlife.arvbotplus.saves.models.GameSave;
import be.effectlife.arvbotplus.saves.models.Skill;
import be.effectlife.arvbotplus.services.TwirkService;
import be.effectlife.arvbotplus.utilities.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class InventoryController implements IController {
    private static final Logger LOG = LoggerFactory.getLogger(InventoryController.class);
    private List<SkillWidgetController> skillWidgetControllerList;
    private List<SkillWidgetController> removedControllerList;

    //region FXML Definitions

    @FXML
    private Pane paneName;

    @FXML
    private Text textName;

    @FXML
    private TextField tfName;

    @FXML
    private VBox vboxSkillOptions;

    @FXML
    private TextArea taItemsArtifacts;

    @FXML
    private TextArea taCluesNotes;

    @FXML
    private Text textCharName;

    @FXML
    private Text textItems;

    @FXML
    private Text textClues;

    @FXML
    private Menu menuFile;

    @FXML
    private MenuItem menuSave;

    @FXML
    private MenuItem menuLoad;

    @FXML
    private MenuItem menuClose;

    @FXML
    private Menu menuTools;

    @FXML
    private MenuItem menuPolls;

    @FXML
    private MenuItem menuDice;

    @FXML
    private MenuItem menuBattle;

    @FXML
    private MenuItem menuConversion;

    @FXML
    private MenuItem menuQuestions;

    @FXML
    private Menu menuHelp;

    @FXML
    private MenuItem menuAbout;

    private Properties properties;
    private String highlight;
    //endregion


    @Override
    public void doInit() {

        menuFile.setText(MessageProperties.getString(MessageKey.SCENE_INVENTORY_MENU_FILE));
        menuLoad.setText(MessageProperties.getString(MessageKey.SCENE_INVENTORY_MENU_FILE_LOAD));
        menuSave.setText(MessageProperties.getString(MessageKey.SCENE_INVENTORY_MENU_FILE_SAVE));
        menuClose.setText(MessageProperties.getString(MessageKey.SCENE_INVENTORY_MENU_FILE_CLOSE));
        menuTools.setText(MessageProperties.getString(MessageKey.SCENE_INVENTORY_MENU_TOOLS));
        menuPolls.setText(MessageProperties.getString(MessageKey.SCENE_INVENTORY_MENU_TOOLS_POLLS));
        menuDice.setText(MessageProperties.getString(MessageKey.SCENE_INVENTORY_MENU_TOOLS_DICE));
        menuBattle.setText(MessageProperties.getString(MessageKey.SCENE_INVENTORY_MENU_TOOLS_BATTLE));
        menuConversion.setText(MessageProperties.getString(MessageKey.SCENE_INVENTORY_MENU_TOOLS_CONVERSION));
        menuHelp.setText(MessageProperties.getString(MessageKey.SCENE_INVENTORY_MENU_HELP));
        menuAbout.setText(MessageProperties.getString(MessageKey.SCENE_INVENTORY_MENU_HELP_ABOUT));
        textCharName.setText(MessageProperties.getString(MessageKey.SCENE_INVENTORY_TEXT_CHARNAME));
        textName.setText(MessageProperties.getString(MessageKey.SCENE_INVENTORY_TEXT_PLACEHOLDER));
        tfName.setText(MessageProperties.getString(MessageKey.SCENE_INVENTORY_TEXT_PLACEHOLDER));
        textClues.setText(MessageProperties.getString(MessageKey.SCENE_INVENTORY_TEXT_CLUESANDNOTES));
        textItems.setText(MessageProperties.getString(MessageKey.SCENE_INVENTORY_TEXT_ITEMSANDARTIFACTS));
        skillWidgetControllerList = new ArrayList<>();
        removedControllerList = new ArrayList<>();
        tfName.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue) {
                tfNameClicked();
            }
        }));
        highlight = ColorHelper.retrieveColor(ColorEnum.HIGHLIGHT);
    }

    @FXML
    void btnAddClicked() {
        createWidget(skillWidgetControllerList.size());
        reloadView();
    }

    @FXML
    void btnCloseClicked() {
        Stage invStage = Main.getStage(Stages.INVENTORY);
        invStage.fireEvent(new WindowEvent(
                invStage,
                WindowEvent.WINDOW_CLOSE_REQUEST
        ));
    }

    @FXML
    void btnPollsClicked() {
        final Stage pollStage = StageBuilder.getStage(Stages.POLL);
        startupTwirkSystem(pollStage);
    }

    @FXML
    public void btnQuestionsClicked() {
        Stage questionStage = StageBuilder.getStage(Stages.QUESTIONS);
        startupTwirkSystem(questionStage);
    }

    private void startupTwirkSystem(Stage twirkStage) {
        if (Main.getTwirkSystem() == null || Main.getTwirkSystem().isNotLoaded()) {
            new Thread(() -> {
                Main.setTwirkSystem(new TwirkService());
                try {
                    Main.getTwirkSystem().initializeSystem(properties, twirkStage, Boolean.parseBoolean(properties.getOrDefault("twitch.disabled", false).toString()));
                    ((ConversionController) AESceneLoader.getInstance().getController(Scenes.S_CONV)).checkTwirk();
                    ((PollController) AESceneLoader.getInstance().getController(Scenes.S_POLL)).onShow();
                } catch (IOException | InterruptedException e) {
                    LOG.error("Error happened while initializing twirksystem", e);
                }
            }).start();
        } else {
            twirkStage.show();
        }
    }

    @FXML
    void btnDiceClicked() {
        StageBuilder.getStage(Stages.DICE).show();
    }

    @FXML
    void btnBattleClicked() {
        StageBuilder.getStage(Stages.BATTLE).show();
    }

    @FXML
    void btnConversionClicked() {
        StageBuilder.getStage(Stages.CONVERSION).show();
    }

    @FXML
    void btnHelpClicked() {
        Platform.runLater(() -> SimplePopup.showPopupInfo("About ArvBot Plus", "This is ArvBot Plus, a twitchbot and Gamebook manager created by me, Effectlife, mainly for fun, and to help Arvan Eleron to have some redundancy in his twitchbots. \n" +
                "This application contains 4 independant tools.\n" +
                "\t1. Inventory: This is the main screen. This window is used to manage all stats concerning playing gamebooks. On the left, there is a list of fully customizable skills and stats. Each skill/stat can have its own name, and can have any value between " + Integer.MIN_VALUE + " and " + Integer.MAX_VALUE + ".\n" +
                " Additionally the second value can be toggled by the checkbox, and the second checkbox enables or disables colors. Under '" + MessageProperties.getString(MessageKey.SCENE_INVENTORY_MENU_FILE) + "' you can also save and load everything.\n" +
                "\t2. Polls: This is used to create polls in twitch chat. There are 2 separate poll systems. The first one is the QuickPoll system: This one only has 2 buttons, and you don't even have to use one of them. It is a simple poll of 2 options, started, closed and data cleared by clicking a single button.\n" +
                " The normal polls are in the section below. You enter a poll question (or leave it blank for '" + MessageProperties.getString(MessageKey.SCENE_POLLS_DEFAULTQUESTION) + "') and at least 2 options. Once you start the poll, the chat can start voting. When closed, a result will be sent to the chat, and the poll doesn't accept new votes.\n" +
                " The poll can then be edited, and started again. The clearing button will first remove all votes, but leave the question/options alone, and when there are no votes anymore, clicking 'clear' again will clear the question and options as well. Also, you can have the modest maximum of " + Integer.MAX_VALUE + " options in a poll (although I would not recommend it :P )\n" +
                "\t3. Dice Roller: This is a simple 'xDy + z' style dice roller, slightly based on the interface of Roll20. In the top row, you enter the amount of dice, the value of each die, and a modifier. When you click on '" + MessageProperties.getString(MessageKey.SCENE_DICE_BUTTON_ROLL) + "', the specified dice will roll, and the result, the calculation and the formula will be shown in the roll history.\n" +
                " This history will contain all rolls from the starting of the program, until it is closed (closing and reopening the Dice Roller will not clear the history, only fully exiting will)\n" +
                "\t4. Battle Manager: This contains a list of enemies with 2 parameters: " + MessageProperties.getString(MessageKey.SCENE_BATTLE_TEXT_VAL_1) + " and " + MessageProperties.getString(MessageKey.SCENE_BATTLE_TEXT_VAL_2) + ". There is no more to it than this."));
    }

    @FXML
    void btnRemoveClicked() {
        if (skillWidgetControllerList.isEmpty()) return;
        SkillWidgetController controllerToRemove = skillWidgetControllerList.get(skillWidgetControllerList.size() - 1);
        for (Node child : vboxSkillOptions.getChildren()) {
            if (Integer.parseInt(child.getUserData().toString()) == controllerToRemove.getId()) {
                vboxSkillOptions.getChildren().clear();
                removedControllerList.add(controllerToRemove);
                skillWidgetControllerList.remove(controllerToRemove);
                break;
            }
        }
        reloadView();
    }
    /*
    inv.skill.default.2.name=Gold
    inv.skill.default.2.value=20
    inv.skill.default.2.maxValue=20
    inv.skill.default.2.hasmaxvalue=false
    */

    public void initializeSkillWidgets() {
        int counter = 0;
        String keyTemplate = "inv.skill.default.%d.%s";

        while (true) {
            String name = properties.getProperty(String.format(keyTemplate, counter, "name"));
            String valueString = properties.getProperty(String.format(keyTemplate, counter, "value"));
            String maxValueString = properties.getProperty(String.format(keyTemplate, counter, "maxValue"));
            boolean hasMaxValue = Boolean.parseBoolean(properties.getProperty(String.format(keyTemplate, counter, "hasmaxvalue")));
            boolean usesColor = Boolean.parseBoolean(properties.getProperty(String.format(keyTemplate, counter, "usescolor")));
            if (name == null) {
                break;
            }
            int value = StringUtils.isNumeric(valueString) ? Integer.parseInt(valueString) : 0;
            int maxValue = StringUtils.isNumeric(maxValueString) ? Integer.parseInt(maxValueString) : 0;
            createWidget(counter, name, value, maxValue, hasMaxValue ? SkillType.MAX : SkillType.SIMPLE, usesColor);
            counter++;

        }
    }

    private void createWidget(int id) {
        createWidget(id, null, 0, 0, SkillType.SIMPLE, false);
    }


    private void createWidget(int id, String name, int value, int maxValue, SkillType skillType, boolean useColors) {
        if (!removedControllerList.isEmpty()) {
            SkillWidgetController remove = removedControllerList.remove(removedControllerList.size() - 1);
            id = remove.getId();
        }
        SceneContainer sceneContainer = AESceneLoader.getInstance().getSceneContainer(Scenes.W_SKILL, "_" + id);
        SkillWidgetController skillWidgetController = (SkillWidgetController) sceneContainer.getController();
        skillWidgetControllerList.add(skillWidgetController);
        Parent root = sceneContainer.getScene().getRoot();
        root.setUserData(id);
        root.toFront();
        skillWidgetController.setId(id);
        String thresholdWarn = properties.getProperty("inv.skill.threshold.warn");
        String thresholdCrit = properties.getProperty("inv.skill.threshold.crit");
        double dThresholdWarn;
        try {
            dThresholdWarn = Double.parseDouble(thresholdWarn);
        } catch (NumberFormatException e) {
            dThresholdWarn = 0;
        }
        double dThresholdCrit;
        try {
            dThresholdCrit = Double.parseDouble(thresholdCrit);
        } catch (NumberFormatException e) {
            dThresholdCrit = 0;
        }

        skillWidgetController.setThresholds(dThresholdWarn, dThresholdCrit);
        if (name != null) {
            skillWidgetController.setName(name); 
            skillWidgetController.setValue(value);
            skillWidgetController.setMaxValue(maxValue);
            skillWidgetController.setType(skillType);
            skillWidgetController.setUseColors(useColors);
        }
        skillWidgetController.reloadView();
        reloadView();
    }

    @FXML
    void btnSaveClicked() {
        SaveManager.saveGame();
    }

    @FXML
    void btnLoadClicked() {
        SaveManager.loadGame();
    }

    @FXML
    void paneNameClicked() {
        if (JFXExtensions.isDoubleClick()) {
            textName.setDisable(true);
            textName.setVisible(false);
            tfName.setDisable(false);
            tfName.setVisible(true);
            paneName.setDisable(true);
            tfName.requestFocus();
        }
    }

    @FXML
    void tfNameClicked() {
        textName.setDisable(false);
        textName.setVisible(true);
        tfName.setDisable(true);
        tfName.setVisible(false);
        paneName.setDisable(false);
        textName.setText(tfName.getText());
    }

    public void hardReset() {
        vboxSkillOptions.getChildren().clear();
        skillWidgetControllerList.clear();
        tfName.setText("");
        taCluesNotes.setText("");
        taItemsArtifacts.setText("");
    }

    @Override
    public void reloadView() {
        vboxSkillOptions.getChildren().clear();
        skillWidgetControllerList.forEach(skillWidgetController -> addWithDragging(vboxSkillOptions, AESceneLoader.getInstance().getScene(Scenes.W_SKILL, "_" + skillWidgetController.getId()).getRoot()));
    }


    public String getName() {
        return textName.getText();
    }

    public String getItemsArtifacts() {
        return taItemsArtifacts.getText();
    }

    public String getCluesNotes() {
        return taCluesNotes.getText();
    }

    public List<SkillWidgetController> getSkills() {
        return skillWidgetControllerList;
    }

    public void load(GameSave gameSave) {
        try {
            textName.setText(gameSave.getName());
            taItemsArtifacts.setText(gameSave.getItemsArtifacts());
            taCluesNotes.setText(gameSave.getCluesNotes());
            List<Skill> skills = gameSave.getSkills();
            for (int i = 0, skillsSize = skills.size(); i < skillsSize; i++) {
                Skill skill = skills.get(i);
                createWidget(i, skill.getName(), skill.getValue(), skill.getMaxValue(), skill.getSkillType(), skill.isUsesColor());
            }
            reloadView();
        } catch (Exception e) {
            SimplePopup.showPopupError("File cannot be loaded, are you sure it is a Skills save?\n\nException: " + e.getMessage() + "\n" + e.getStackTrace()[0]);
            LOG.error("File cannot be loaded", e);
        }
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }


    public void disableTwirkMenus(boolean disable) {
        menuPolls.setDisable(disable);
        menuQuestions.setDisable(disable);
    }

    private void addWithDragging(final VBox root, final Parent node) {
        node.setOnDragDetected(event -> node.startFullDrag());

        // next two handlers just an idea how to show the drop target visually:
        node.setOnMouseDragEntered(event -> {
            node.getChildrenUnmodifiable().forEach(child -> child.setMouseTransparent(true));
            node.setStyle("-fx-border-color: " + highlight + "; -fx-border-width: 0 0 2 0;");
        });
        node.setOnMouseDragExited(event -> {
            node.getChildrenUnmodifiable().forEach(child -> child.setMouseTransparent(false));
            node.setStyle("");
        });

        node.setOnMouseDragReleased(event -> {
            node.setStyle("");
            int indexOfDraggingNode = root.getChildren().indexOf(event.getGestureSource());
            int indexOfDropTarget = root.getChildren().indexOf(node);
            if (indexOfDropTarget < indexOfDraggingNode) indexOfDropTarget++;
            rotateNodes(root, indexOfDraggingNode, indexOfDropTarget);
            event.consume();
        });
        root.getChildren().add(node);
    }

    private void rotateNodes(final VBox root, final int indexOfDraggingNode,
                             final int indexOfDropTarget) {
        if (indexOfDraggingNode >= 0 && indexOfDropTarget >= 0) {
            final Node node = root.getChildren().remove(indexOfDraggingNode);
            root.getChildren().add(indexOfDropTarget, node);
            SkillWidgetController controller = skillWidgetControllerList.remove(indexOfDraggingNode);
            skillWidgetControllerList.add(indexOfDropTarget, controller);
            reloadView();
        }
    }
}
