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
    void cbHasMax_Switched(ActionEvent event) {
        spinnerMaxValue.setVisible(cbHasMax.isSelected());
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
        spinnerCurrentValue.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
        spinnerMaxValue.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
    }

    public void setType(SkillType skillType) {
        switch (skillType) {
            case MAX:
                spinnerMaxValue.setVisible(true);
                break;
            case SIMPLE:
                spinnerMaxValue.setVisible(false);
        }
    }

    @Override
    public void onShow() {

    }

    @Override
    public void reloadView() {

    }
}
