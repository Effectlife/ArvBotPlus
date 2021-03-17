package be.effectlife.arvbotplus.controllers.widgets;

import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.utilities.JFXExtensions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
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
    private Spinner<?> spinnerCurrentValue;

    @FXML
    private Spinner<?> spinnerMaxValue;

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

    }

    @Override
    public void onShow() {

    }

    @Override
    public void reloadView() {

    }
}
