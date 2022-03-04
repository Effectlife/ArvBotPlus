package be.effectlife.arvbotplus.controllers.widgets;

import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.utilities.JFXExtensions;
import be.effectlife.arvbotplus.utilities.SkillType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class SkillWidgetController implements IController {
    private static final String CSS_WARN_TEXT_CLASS = "warn-text";
    private static final String CSS_CRIT_TEXT_CLASS = "crit-text";
    private int id;
    private SkillType skillType;
    private double thresholdWarn;
    private double thresholdCrit;
    @FXML
    private Pane paneSkillname;

    @FXML
    private Text textSkillname;

    @FXML
    private TextField tfSkillname;

    @FXML
    private Spinner<Integer> spinnerCurrentValue;

    @FXML
    private Spinner<Integer> spinnerMaxValue;

    @FXML
    private CheckBox cbHasMax;

    @FXML
    private CheckBox cbUseColor;

    @FXML
    void cbHasMaxSwitched(ActionEvent event) {
        setType(cbHasMax.isSelected() ? SkillType.MAX : SkillType.SIMPLE);
    }

    @FXML
    void cbUseColorSwitched(ActionEvent event) {
        if (cbUseColor.isSelected()) {
            reloadView();
        } else {
            spinnerCurrentValue.getStyleClass().remove(CSS_WARN_TEXT_CLASS);
            spinnerCurrentValue.getStyleClass().remove(CSS_CRIT_TEXT_CLASS);
        }
    }

    @FXML
    void paneSkillnameClicked(MouseEvent event) {
        if (JFXExtensions.isDoubleClick()) {
            textSkillname.setDisable(true);
            textSkillname.setVisible(false);
            tfSkillname.setDisable(false);
            tfSkillname.setVisible(true);
            paneSkillname.setDisable(true);
            tfSkillname.requestFocus();
        }
    }

    @FXML
    void tfSkillnameClicked(ActionEvent event) {
        textSkillname.setDisable(false);
        textSkillname.setVisible(true);
        tfSkillname.setDisable(true);
        tfSkillname.setVisible(false);
        paneSkillname.setDisable(false);
        textSkillname.setText(tfSkillname.getText());
    }

    @Override
    public void doInit() {
        spinnerCurrentValue.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 0));
        spinnerMaxValue.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 0));
        tfSkillname.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue) {
                tfSkillnameClicked(null);
            }
        }));
        spinnerCurrentValue.valueProperty().addListener(((observable, oldValue, newValue) -> reloadView()));
        spinnerMaxValue.valueProperty().addListener(((observable, oldValue, newValue) -> reloadView()));
    }

    @Override
    public void reloadView() {
        if (skillType == SkillType.MAX && cbUseColor.isSelected()) {

            if (getValue() < getMaxValue() * thresholdCrit) {
                if (!spinnerCurrentValue.getStyleClass().contains(CSS_CRIT_TEXT_CLASS)) {
                    spinnerCurrentValue.getStyleClass().remove(CSS_WARN_TEXT_CLASS);
                    spinnerCurrentValue.getStyleClass().add(CSS_CRIT_TEXT_CLASS);
                }
            } else if (getValue() < getMaxValue() * thresholdWarn) {
                if (!spinnerCurrentValue.getStyleClass().contains(CSS_WARN_TEXT_CLASS)) {
                    spinnerCurrentValue.getStyleClass().remove(CSS_CRIT_TEXT_CLASS);
                    spinnerCurrentValue.getStyleClass().add(CSS_WARN_TEXT_CLASS);
                }
            } else {
                spinnerCurrentValue.getStyleClass().remove(CSS_WARN_TEXT_CLASS);
                spinnerCurrentValue.getStyleClass().remove(CSS_CRIT_TEXT_CLASS);
            }
        }
    }

    public void setName(String name) {
        this.textSkillname.setText(name);
        this.tfSkillname.setText(name);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSkillname() {
        return textSkillname.getText();
    }

    public SkillType getType() {
        return skillType;
    }

    public void setType(SkillType skillType) {
        this.skillType = skillType;
        if (skillType == SkillType.MAX) {
            spinnerMaxValue.setVisible(true);
            cbHasMax.setSelected(true);
            cbUseColor.setVisible(true);
        } else {
            spinnerCurrentValue.getStyleClass().remove(CSS_WARN_TEXT_CLASS);
            spinnerCurrentValue.getStyleClass().remove(CSS_CRIT_TEXT_CLASS);
            spinnerMaxValue.setVisible(false);
            cbHasMax.setSelected(false);
            cbUseColor.setVisible(false);

        }
        reloadView();
    }

    public int getValue() {
        return spinnerCurrentValue.getValue();
    }

    public void setValue(int value) {
        this.spinnerCurrentValue.getValueFactory().setValue(value);
    }

    public int getMaxValue() {
        return spinnerMaxValue.getValue();
    }

    public void setMaxValue(int value) {
        this.spinnerMaxValue.getValueFactory().setValue(value);
    }

    public void setThresholds(double thresholdWarn, double thresholdCrit) {
        this.thresholdWarn = thresholdWarn;
        this.thresholdCrit = thresholdCrit;
    }

    public boolean isUseColors() {
        return cbUseColor.isSelected();
    }

    public void setUseColors(boolean useColors) {
        cbUseColor.setSelected(useColors);
    }
}
