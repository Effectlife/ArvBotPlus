package be.effectlife.arvbotplus.controllers.scenes;

import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.controllers.widgets.SkillWidgetController;
import be.effectlife.arvbotplus.loading.*;
import be.effectlife.arvbotplus.saves.SaveManager;
import be.effectlife.arvbotplus.saves.models.Save;
import be.effectlife.arvbotplus.saves.models.Skill;
import be.effectlife.arvbotplus.twirk.TwirkSystem;
import be.effectlife.arvbotplus.utilities.JFXExtensions;
import be.effectlife.arvbotplus.utilities.SkillType;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static be.effectlife.arvbotplus.Main.twirkSystem;


//TODO: Implement the complete inventory experience
public class InventoryController implements IController {
    private static final Logger LOG = LoggerFactory.getLogger(InventoryController.class);
    private List<SkillWidgetController> skillWidgetControllerList;

    //region FXML Definitions
    @FXML
    private GridPane base;
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

    private Properties properties;
    //endregion


    @FXML
    void btnAdd_Clicked(ActionEvent event) {
        createWidget(skillWidgetControllerList.size());
        reloadView();
    }

    @FXML
    void btnClose_Clicked(ActionEvent event) {

    }

    @FXML
    void btnPolls_Clicked(ActionEvent event) {
        final Stage pollStage = StageBuilder.getStage(Stages.POLL);
        new Thread(() -> {
            twirkSystem = new TwirkSystem();
            try {
                twirkSystem.initializeSystem(properties, false);
                Platform.runLater(pollStage::show);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    void btnDice_Clicked(ActionEvent event) {
        StageBuilder.getStage(Stages.DICE).show();
    }

    @FXML
    void btnBattle_Clicked(ActionEvent event) {

    }

    @FXML
    void btnRemove_Clicked(ActionEvent event) {
        if (skillWidgetControllerList.size() == 0) return;
        SkillWidgetController controllerToRemove = skillWidgetControllerList.get(skillWidgetControllerList.size() - 1);
        for (Node child : vboxSkillOptions.getChildren()) {
            if (Integer.parseInt(child.getUserData().toString()) == controllerToRemove.getId()) {
                vboxSkillOptions.getChildren().remove(child);
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
        SceneContainer sceneContainer = AESceneLoader.getInstance().getSceneContainer(Scenes.W_SKILL, "_" + id);
        SkillWidgetController skillWidgetController = (SkillWidgetController) sceneContainer.getController();
        skillWidgetControllerList.add(skillWidgetController);
        vboxSkillOptions.getChildren().add(sceneContainer.getScene().getRoot());
        sceneContainer.getScene().getRoot().setUserData(id);
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
    }

    @FXML
    void btnSave_Clicked(ActionEvent event) {
        SaveManager.saveGame();
    }

    @FXML
    void btnLoad_Clicked(ActionEvent event) {
        SaveManager.loadGame();
    }

    @FXML
    void paneName_Clicked(MouseEvent event) {
        if (JFXExtensions.isDoubleClick()) {
            textName.setDisable(true);
            textName.setVisible(false);
            tfName.setDisable(false);
            tfName.setVisible(true);
            paneName.setDisable(true);
        }
    }

    @FXML
    void tfName_Clicked(ActionEvent event) {
        textName.setDisable(false);
        textName.setVisible(true);
        tfName.setDisable(true);
        tfName.setVisible(false);
        paneName.setDisable(false);
        textName.setText(tfName.getText());
    }


    @Override
    public void doInit() {
        skillWidgetControllerList = new ArrayList<>();
        tfName.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue) {
                tfName_Clicked(null);
            }
        }));
    }

    public void hardReset() {
        vboxSkillOptions.getChildren().clear();
        skillWidgetControllerList.clear();
        tfName.setText("");
        taCluesNotes.setText("");
        taItemsArtifacts.setText("");
    }

    @Override
    public void onShow() {

    }

    @Override
    public void reloadView() {
        vboxSkillOptions.getChildren().clear();
        skillWidgetControllerList.forEach(skillWidgetController -> vboxSkillOptions.getChildren().add(AESceneLoader.getInstance().getScene(Scenes.W_SKILL, "_" + skillWidgetController.getId()).getRoot()));
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

    public void load(Save save) {
        textName.setText(save.getName());
        taItemsArtifacts.setText(save.getItemsArtifacts());
        taCluesNotes.setText(save.getCluesNotes());
        List<Skill> skills = save.getSkills();
        for (int i = 0, skillsSize = skills.size(); i < skillsSize; i++) {
            Skill skill = skills.get(i);
            createWidget(i, skill.getName(), skill.getValue(), skill.getMaxValue(), skill.getSkillType(), skill.isUsesColor());
        }
        reloadView();
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
