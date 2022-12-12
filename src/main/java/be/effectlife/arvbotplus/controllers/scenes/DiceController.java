package be.effectlife.arvbotplus.controllers.scenes;

import be.effectlife.arvbotplus.Main;
import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.controllers.widgets.DiceResultController;
import be.effectlife.arvbotplus.loading.*;
import be.effectlife.arvbotplus.models.dice.DieRoll;
import be.effectlife.arvbotplus.utilities.DiceUtility;
import be.effectlife.arvbotplus.utilities.SimplePopup;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.*;


public class DiceController implements IController {
    private static final Logger LOG = LoggerFactory.getLogger(DiceController.class);
    private static final Random random = new SecureRandom();
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

    @FXML
    private TextField tfCustomRoll;

    @FXML
    private CheckBox cbShowChatRolls;

    private int counter = 0;
    private List<DiceResultController> diceResultControllers;

    @Override
    public void doInit() {

        setupSpinner(1, 2, spinnerDiceCount);

        setupSpinner(2, 6, spinnerDiceValue);

        setupSpinner(Integer.MIN_VALUE, 0, spinnerModifier);

        textD.setText(MessageProperties.getString(MessageKey.SCENE_DICE_TEXT_D));
        textRoll.setText(MessageProperties.getString(MessageKey.SCENE_DICE_TEXT_ROLL));
        btnRoll.setText(MessageProperties.getString(MessageKey.SCENE_DICE_BUTTON_ROLL));

        diceResultControllers = new ArrayList<>();
        btnRollClicked();
        reloadView();
        diceResultControllers.clear();
        counter = 0;
        reloadView();
    }

    @Override
    @SuppressWarnings("squid:S2142")
    public void reloadView() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            LOG.error("Exception happened", e);
        }
        vboxHistory.getChildren().clear();

        int limit = 50;
        for (int i = Math.max(0, diceResultControllers.size() - limit); i < diceResultControllers.size(); i++) {
            try {
                diceResultControllers.get(i).reloadView();
                vboxHistory.getChildren().add(0, AESceneLoader.getInstance().getScene(Scenes.W_DICERESULT, "_" + i).getRoot());
            } catch (IndexOutOfBoundsException e) {
                LOG.error("Exception happened", e);
            }
        }
    }

    /**
     * Origin UI -> Definately do the display code (StringBuilder & DiceResult Widget)
     */
    public void btnRollClicked() {
        doRoll(true, null, null);
    }

    public DieRoll doRoll(boolean showInUI, String fromChat, String sender) {

        StringBuilder sb = new StringBuilder("<p>&ensp;");
        StringBuilder sbt = new StringBuilder();
        DieRoll result = null;
        if (fromChat == null) {
            if (StringUtils.isNotBlank(tfCustomRoll.getText())) {
                result = doRollInternalCustom(sb, sbt, null, null);
            } else {
                result = doRollInternalStandard(sb, sbt);
            }
        } else {
            result = doRollInternalCustom(sb, sbt, fromChat, sender);
        }
        if (showInUI) {
            //Add new Result
            showRollInUI(sb, sbt, result, sender);
        }
        return result;
    }

    private void showRollInUI(StringBuilder sb, StringBuilder sbt, DieRoll result, String sender) {
        if (result == null) {
            LOG.error("Trying to show a null result for: {}", sbt);
            return;
        }
        SceneContainer diceResult = AESceneLoader.getInstance().getSceneContainer(Scenes.W_DICERESULT, "_" + counter);
        counter++;

        DiceResultController diceResultController = (DiceResultController) diceResult.getController();
        diceResultControllers.add(diceResultController);
        diceResultController.setTextResult("" + result.getValue());
        sb.append("<p/>");
        diceResultController.setTextCalculation(sb.toString());

        diceResultController.setTextTemplate(sbt.toString());
        diceResultController.setSender(sender);
        diceResultController.loadStyle();
        reloadView();
    }

    public void doRollChat(String contentFiltered, String sender) {
        Map<String, String> params = new HashMap<>();
        try {
            DieRoll dieRoll = doRoll(cbShowChatRolls.isSelected(), contentFiltered, sender);

            params.put("roll", String.valueOf(dieRoll.getValue()));
            params.put("expression", dieRoll.getExpression());
            params.put("sender", sender);
            Main.getTwirkService().channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_DO_ROLL, params));
        } catch (IllegalArgumentException iae) {
            if (iae.getMessage().contains("Operator is unknown for token")) {
                params.put("error", "Unknown operator in expression. Please check if your expression is valid.");
            } else {
                params.put("error", iae.getMessage());
            }
            Main.getTwirkService().channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_ROLL_FAILED, params));
        }
    }

    private DieRoll doRollInternalStandard(StringBuilder sb, StringBuilder sbt) {
        sb.append("(");
        int diceCount = spinnerDiceCount.getValue();
        int diceValue = spinnerDiceValue.getValue();
        int modifier = spinnerModifier.getValue();
        double result = 0;
        for (int i = 0; i < diceCount; i++) {
            int diceRoll = random.nextInt(diceValue) + 1;

            if (diceRoll == diceValue) {
                sb.append(DiceUtility.addSuccess(diceRoll));
            } else if (diceRoll == 1) {
                sb.append(DiceUtility.addFail());
            } else {
                sb.append(DiceUtility.addNormal(diceRoll));
            }
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

        sbt.append(diceCount).append("d").append(diceValue);
        if (modifier > 0) {
            sbt.append(" + ").append(modifier);
        } else if (modifier < 0) {
            sbt.append(" - ").append(Math.abs(modifier));
        }
        return DieRoll.createSimpleResult(result);
    }

    private DieRoll doRollInternalCustom(StringBuilder sb, StringBuilder sbt, String fromChat, String sender) {
        DieRoll dieRoll = null;
        try {
            if (fromChat == null) {
                dieRoll = DieRoll.parseRoll(tfCustomRoll.getText());
            } else {
                dieRoll = DieRoll.parseRoll(fromChat);
            }
            sb.append("<span>").append(dieRoll.getExpressionHtml()).append("</span>");
            sbt.append(dieRoll.getRawExpression());
        } catch (RuntimeException exception) {
            if (fromChat == null) {
                Platform.runLater(() -> SimplePopup.showPopupError("Cannot read the given part of the roll: " + exception.getMessage()));
            } else {
                //Chat
                Map<String, String> params = new HashMap<>();
                params.put("error", exception.getMessage());
                params.put("sender", sender);
                Main.getTwirkService().channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_ROLL_FAILED, params));

            }
        }

        return dieRoll;
    }

    private void setupSpinner(int minValue, int initialValue, Spinner<Integer> spinnerModifier) {
        SpinnerValueFactory<Integer> spinnerModifierFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(minValue, Integer.MAX_VALUE, initialValue);
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
