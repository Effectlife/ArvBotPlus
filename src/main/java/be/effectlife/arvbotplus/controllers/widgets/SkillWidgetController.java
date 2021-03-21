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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkillWidgetController implements IController {
    private static final Logger LOG = LoggerFactory.getLogger(SkillWidgetController.class);
    private int id;
    private SkillType skillType;
    private double thresholdWarn, thresholdCrit;

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
    void cbHasMax_Switched(ActionEvent event) {
        setType(cbHasMax.isSelected() ? SkillType.MAX : SkillType.SIMPLE);
    }

    @FXML
    void cbUseColor_Switched(ActionEvent event) {
        if (cbUseColor.isSelected()) {
            reloadView();
        } else {
            spinnerCurrentValue.getStyleClass().remove("warn-text");
            spinnerCurrentValue.getStyleClass().remove("crit-text");
        }
    }

    @FXML
    void paneSkillname_Clicked(MouseEvent event) {
        if (JFXExtensions.isDoubleClick()) {
            textSkillname.setDisable(true);
            textSkillname.setVisible(false);
            tfSkillname.setDisable(false);
            tfSkillname.setVisible(true);
            paneSkillname.setDisable(true);
        }
    }

    @FXML
    void tfSkillname_Clicked(ActionEvent event) {
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
                tfSkillname_Clicked(null);
            }
        }));
        spinnerCurrentValue.valueProperty().addListener(((observable, oldValue, newValue) -> reloadView()));
        spinnerMaxValue.valueProperty().addListener(((observable, oldValue, newValue) -> reloadView()));
    }

    public void setType(SkillType skillType) {
        this.skillType = skillType;
        if (skillType == SkillType.MAX) {
            spinnerMaxValue.setVisible(true);
            cbHasMax.setSelected(true);
            cbUseColor.setVisible(true);
        } else {
            spinnerCurrentValue.getStyleClass().remove("warn-text");
            spinnerCurrentValue.getStyleClass().remove("crit-text");
            spinnerMaxValue.setVisible(false);
            cbHasMax.setSelected(false);
            cbUseColor.setVisible(false);

        }
        reloadView();
    }

    @Override
    public void onShow() {

    }

    @Override
    public void reloadView() {
        if (skillType == SkillType.MAX && cbUseColor.isSelected()) {

            if (getValue() < getMaxValue() * thresholdCrit) {
                if (!spinnerCurrentValue.getStyleClass().contains("crit-text")) {
                    spinnerCurrentValue.getStyleClass().remove("warn-text");
                    spinnerCurrentValue.getStyleClass().add("crit-text");
                }
            } else if (getValue() < getMaxValue() * thresholdWarn) {
                if (!spinnerCurrentValue.getStyleClass().contains("warn-text")) {
                    spinnerCurrentValue.getStyleClass().remove("crit-text");
                    spinnerCurrentValue.getStyleClass().add("warn-text");
                }
            } else {
                spinnerCurrentValue.getStyleClass().remove("warn-text");
                spinnerCurrentValue.getStyleClass().remove("crit-text");
            }
        }
    }

    public void setName(String name) {
        this.textSkillname.setText(name);
        this.tfSkillname.setText(name);
    }

    public void setValue(int value) {
        this.spinnerCurrentValue.getValueFactory().setValue(value);
    }

    public void setMaxValue(int value) {
        this.spinnerMaxValue.getValueFactory().setValue(value);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getSkillname() {
        return textSkillname.getText();
    }

    public SkillType getType() {
        return skillType;
    }

    public int getValue() {
        return spinnerCurrentValue.getValue();
    }

    public int getMaxValue() {
        return spinnerMaxValue.getValue();
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
