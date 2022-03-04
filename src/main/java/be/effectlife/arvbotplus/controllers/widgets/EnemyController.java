package be.effectlife.arvbotplus.controllers.widgets;

import be.effectlife.arvbotplus.controllers.IController;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
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
        setupSpinner(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, spinnerSkill);
        setupSpinner(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, spinnerStamina);
    }

    private void setupSpinner(int minValue, int maxValue, int initialValue, Spinner<Integer> spinnerModifier) {
        SpinnerValueFactory<Integer> spinnerModifierFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(minValue, maxValue, initialValue);
        spinnerModifier.setValueFactory(spinnerModifierFactory);
        spinnerModifier.setEditable(true);
        // hook in a formatter with the same properties as the factory
        TextFormatter<Integer> modifierFormatter = new TextFormatter<>(spinnerModifierFactory.getConverter(), spinnerModifierFactory.getValue());
        spinnerModifier.getEditor().setTextFormatter(modifierFormatter);
        // bidi-bind the values
        spinnerModifierFactory.valueProperty().bindBidirectional(modifierFormatter.valueProperty());
    }

    @Override
    public void reloadView() {
        //Does not have any fields to reload
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void clear() {
        this.tfDescription.setText("");
        this.spinnerSkill.getValueFactory().setValue(0);
        this.spinnerStamina.getValueFactory().setValue(0);
    }
}
