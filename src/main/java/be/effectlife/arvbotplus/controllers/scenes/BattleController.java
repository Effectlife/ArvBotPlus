package be.effectlife.arvbotplus.controllers.scenes;

import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.controllers.widgets.EnemyController;
import be.effectlife.arvbotplus.loading.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class BattleController implements IController {

    @FXML
    private GridPane gpBase;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnRemove;

    @FXML
    private Button btnClear;
    @FXML
    private Text textVal1;

    @FXML
    private Text textVal2;
    @FXML
    private VBox vboxEnemies;

    private List<EnemyController> enemyControllerList;

    @FXML
    void btnAddClicked(ActionEvent event) {
        createWidget(enemyControllerList.size());
        reloadView();
    }

    @FXML
    void btnRemoveClicked(ActionEvent event) {
        if (enemyControllerList.size() <= 1) return; // never remove the first option
        EnemyController controllerToRemove = enemyControllerList.get(enemyControllerList.size() - 1);
        for (Node child : vboxEnemies.getChildren()) {
            if (Integer.parseInt(child.getUserData().toString()) == controllerToRemove.getId()) {
                vboxEnemies.getChildren().remove(child);
                enemyControllerList.remove(controllerToRemove);
                break;
            }
        }
        reloadView();
    }

    @FXML
    void btnClearClicked(ActionEvent event) {
        enemyControllerList.forEach(EnemyController::clear);
    }

    @Override
    public void doInit() {
        btnAdd.setText(MessageProperties.getString(MessageKey.SCENE_BATTLE_BUTTON_ADD));
        btnClear.setText(MessageProperties.getString(MessageKey.SCENE_BATTLE_BUTTON_CLEAR));
        btnRemove.setText(MessageProperties.getString(MessageKey.SCENE_BATTLE_BUTTON_REMOVE));
        textVal1.setText(MessageProperties.getString(MessageKey.SCENE_BATTLE_TEXT_VAL_1));
        textVal2.setText(MessageProperties.getString(MessageKey.SCENE_BATTLE_TEXT_VAL_2));

        enemyControllerList = new ArrayList<>();
        btnAddClicked(null);
    }

    @Override
    public void reloadView() {
        vboxEnemies.getChildren().clear();
        enemyControllerList.forEach(enemyController -> vboxEnemies.getChildren().add(AESceneLoader.getInstance().getScene(Scenes.W_ENEMY, "_" + enemyController.getId()).getRoot()));

    }

    private void createWidget(int id) {
        SceneContainer sceneContainer = AESceneLoader.getInstance().getSceneContainer(Scenes.W_ENEMY, "_" + id);
        EnemyController enemyController = (EnemyController) sceneContainer.getController();
        enemyControllerList.add(enemyController);
        vboxEnemies.getChildren().add(sceneContainer.getScene().getRoot());
        sceneContainer.getScene().getRoot().setUserData(id);
        enemyController.setId(id);
        enemyController.reloadView();
    }
}
