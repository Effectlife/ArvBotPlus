package be.effectlife.arvbotplus.controllers.scenes;

import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.controllers.widgets.DiceResultController;
import be.effectlife.arvbotplus.loading.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DiceController implements IController {
    private static Logger LOG = LoggerFactory.getLogger(DiceController.class);
    private static Random random;
    @FXML
    private Text textRoll;

    @FXML
    private Text textD;

    @FXML
    private Spinner<Integer> spinnerDiceCount;

    @FXML
    private Spinner<Integer> spinnerDiceValue;

    @FXML
    private Spinner<Integer> spinnerModifier;

    @FXML
    private VBox vboxHistory;

    @FXML
    private Pane paneBackgroundCrit;

    @FXML
    private Pane paneBackgroundSuccess;

    @FXML
    private Button btnRoll;

    private int counter = 0;
    private List<DiceResultController> diceResultControllers;

    @Override
    public void doInit() {

        setupSpinner(1, Integer.MAX_VALUE, 2, spinnerDiceCount);

        setupSpinner(2, Integer.MAX_VALUE, 6, spinnerDiceValue);

        setupSpinner(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, spinnerModifier);

        textD.setText(MessageProperties.getString(MessageKey.SCENE_DICE_TEXT_D));
        textRoll.setText(MessageProperties.getString(MessageKey.SCENE_DICE_TEXT_ROLL));
        btnRoll.setText(MessageProperties.getString(MessageKey.SCENE_DICE_BUTTON_ROLL));

        random = new SecureRandom();
        diceResultControllers = new ArrayList<>();
        btnRoll_Clicked(null);
        reloadView();
        diceResultControllers.clear();
        counter = 0;
        reloadView();
    }

    @Override
    public void onShow() {

    }

    @Override
    public void reloadView() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        vboxHistory.getChildren().clear();

        int limit = 50;
        for (int i = diceResultControllers.size() - limit; i < diceResultControllers.size(); i++) {
            try {
                diceResultControllers.get(i).reloadView();
                vboxHistory.getChildren().add(0, AESceneLoader.getInstance().getScene(Scenes.W_DICERESULT, "_" + i).getRoot());
            } catch (IndexOutOfBoundsException ignored) {
            }
        }
    }


    public void btnRoll_Clicked(ActionEvent actionEvent) {
        int diceCount = spinnerDiceCount.getValue();
        int diceValue = spinnerDiceValue.getValue();
        int modifier = spinnerModifier.getValue();
        int result = 0;
        StringBuilder sb = new StringBuilder("<p>&ensp;(");
        for (int i = 0; i < diceCount; i++) {
            int diceRoll = random.nextInt(diceValue) + 1;

            if (diceRoll == diceValue) {
                sb.append("<span class=\"color-success\">");
            } else if (diceRoll == 1) {
                sb.append("<span class=\"color-crit\">");
            } else {
                sb.append("<span>");
            }
            sb.append(diceRoll).append("</span>");
            if (i != diceCount - 1) {
                sb.append(" + ");
            } else if (modifier > 0) {
                sb.append(") + ").append(modifier);
            } else if (modifier < 0) {
                sb.append(") - ").append(Math.abs(modifier));
            } else {
                sb.append(")");
            }
            result += diceRoll;
        }
        result += modifier;
        //Add new Result
        SceneContainer diceResult = AESceneLoader.getInstance().getSceneContainer(Scenes.W_DICERESULT, "_" + counter);
        counter++;

        DiceResultController diceResultController = (DiceResultController) diceResult.getController();
        diceResultControllers.add(diceResultController);
        diceResultController.setTextResult("" + result);
        sb.append("<p/>");
        diceResultController.setTextCalculation(sb.toString());
        StringBuilder sbt = new StringBuilder("");
        sbt.append(diceCount).append("d").append(diceValue);
        if (modifier > 0) {
            sbt.append(" + ").append(modifier);
        } else if (modifier < 0) {
            sbt.append(" - ").append(Math.abs(modifier));
        }
        diceResultController.setTextTemplate(sbt.toString());
        diceResultController.loadStyle();
        reloadView();
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


    public void reloadWidth(double newValue) {
        vboxHistory.setPrefWidth(newValue);
        for (DiceResultController diceResultController : diceResultControllers) {
            diceResultController.reloadWidth(newValue);
        }
    }
}
