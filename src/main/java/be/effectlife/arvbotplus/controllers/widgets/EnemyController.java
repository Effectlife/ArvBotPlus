package be.effectlife.arvbotplus.controllers.widgets;

import be.effectlife.arvbotplus.controllers.IController;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class EnemyController implements IController {

    private int id;
    @FXML
    private GridPane gpBase;

    @FXML
    private TextField tfDescription;

    @FXML
    private Spinner<Integer> spinnerSkill;

    @FXML
    private Spinner<Integer> spinnerStamina;

    @Override
    public void doInit() {
        spinnerSkill.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 0));
        spinnerStamina.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 0));
    }

    @Override
    public void onShow() {

    }

    @Override
    public void reloadView() {

    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void clear() {
        this.tfDescription.setText("");
        this.spinnerSkill.getValueFactory().setValue(0);
        this.spinnerStamina.getValueFactory().setValue(0);
    }
}
