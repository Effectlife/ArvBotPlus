package be.effectlife.arvbotplus.controllers.scenes;

import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.controllers.widgets.SkillWidgetController;
import be.effectlife.arvbotplus.saves.SaveManager;
import be.effectlife.arvbotplus.utilities.JFXExtensions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


//TODO: Implement the complete inventory experience
public class InventoryController implements IController {
    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryController.class);
    private List<SkillWidgetController> skillWidgetControllerLists;

    //region FXML Definitions
    @FXML
    private GridPane base;
    @FXML
    private Pane paneName;

    @FXML
    private Text textName;

    @FXML
    private Button btnLoad;

    @FXML
    private Button btnSave;

    @FXML
    private TextField tfName;
    //endregion


    @FXML
    void btnAddSimple_Clicked(ActionEvent event) {

    }

    @FXML
    void btnRemoveSimple_Clicked(ActionEvent event) {

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
        skillWidgetControllerLists = new ArrayList<>();
    }

    @Override
    public void onShow() {

    }

    @Override
    public void reloadView() {

    }


}
