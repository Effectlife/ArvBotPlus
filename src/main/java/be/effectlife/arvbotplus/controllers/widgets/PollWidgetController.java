package be.effectlife.arvbotplus.controllers.widgets;

import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.controllers.scenes.PollController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.MessageKey;
import be.effectlife.arvbotplus.loading.MessageProperties;
import be.effectlife.arvbotplus.loading.Scenes;
import be.effectlife.arvbotplus.utilities.JFXExtensions;
import be.effectlife.arvbotplus.utilities.SimplePopup;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PollWidgetController implements IController {
    @FXML
    private ProgressBar pBarVotes;

    @FXML
    private Spinner<Integer> spinnerVotes;

    @FXML
    private Button btnClear;

    @FXML
    private TextField tfOption;
    @FXML
    private Text textId;
    private PollController pollController;
    private Set<String> voters;
    private int optionId;
    @FXML
    private GridPane gpHover;
    @FXML
    private GridPane gpBase;

    @FXML
    private Pane paneHoverTop;

    @FXML
    private Pane paneHoverBottom;
    @FXML
    void pBarVotesClicked(MouseEvent event) {
        if (!voters.isEmpty()) {
            String reduce = voters.stream().sorted().reduce("", (id, voter) -> id += voter + "\n");
            Platform.runLater(() -> SimplePopup.showPopupInfo("Voters for option " + (optionId + 1), reduce));
        }
    }

    @FXML
    void btnClearClicked(ActionEvent event) {
        if (event != null && isCleared()) {
            tfOption.setText("");
        }
        spinnerVotes.getValueFactory().setValue(0);
        pBarVotes.setProgress(0);
    }


    @Override
    public void doInit() {
        pollController = (PollController) AESceneLoader.getInstance().getController(Scenes.S_POLL);
        spinnerVotes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
        voters = new HashSet<>();
        btnClear.setText(MessageProperties.getString(MessageKey.WIDGET_POLLS_BUTTON_CLEAR));
        btnClearClicked(null);
        tfOption.addEventFilter(KeyEvent.KEY_PRESSED, JFXExtensions.tabTraverse);
        spinnerVotes.valueProperty().addListener(((observable, oldValue, newValue) -> pollController.reloadView()));
        gpBase.setStyle("-fx-border-color: transparent; -fx-border-width: 1 0 1 0;");

    }

    @Override
    public void reloadView() {
        int totalVotes = pollController.getHighestAmountOfVotes();
        if (spinnerVotes.getValue() == 0) {
            pBarVotes.setProgress(0);
        } else {
            pBarVotes.setProgress((double) spinnerVotes.getValue() / totalVotes);
        }
    }

    public void clear(boolean votesCleared) {
        if (votesCleared) {
            tfOption.setText("");
        }
        btnClearClicked(null);
    }

    public String getOptionText() {
        return tfOption.getText();
    }

    public void setOptionText(String s) {
        tfOption.setText(s);
    }

    public void disableButtons(boolean disabled) {

        btnClear.setDisable(disabled);
        if (StringUtils.isBlank(tfOption.getText())) {
            spinnerVotes.setDisable(disabled);
            tfOption.setDisable(disabled);
        } else {
            spinnerVotes.setDisable(false);
            tfOption.setDisable(false);
        }
    }

    public int getOptionId() {
        return optionId;
    }

    public void setOptionId(int optionId) {
        this.optionId = optionId;
        textId.setText(optionId + 1 + "");
    }

    public Set<String> getVoters() {
        return voters;
    }

    public void setVoters(List<String> voters) {
        this.voters.clear();
        this.voters.addAll(voters);
    }

    public int getVotes() {
        return spinnerVotes.getValue();
    }

    public void setVotes(int votes) {
        spinnerVotes.getValueFactory().setValue(votes);
    }

    public void vote(String sender) {
        spinnerVotes.getValueFactory().setValue(spinnerVotes.getValue() + 1);
        voters.add(sender);
    }

    public void removeVote(String sender) {
        spinnerVotes.getValueFactory().setValue(spinnerVotes.getValue() - 1);
        voters.remove(sender);
    }

    public void resetVotes() {
        voters.clear();
        spinnerVotes.getValueFactory().setValue(0);
    }

    public boolean isCleared() {
        return spinnerVotes.getValue() == 0 && pBarVotes.getProgress() == 0;
    }

    public void copyData(PollWidgetController that) {
        this.voters = that.getVoters();

        if (this.tfOption == null) this.tfOption = new TextField();
        this.tfOption.setText(that.tfOption.getText());

        if (this.pBarVotes == null) this.pBarVotes = new ProgressBar(that.pBarVotes.getProgress());
        else this.pBarVotes.setProgress(that.pBarVotes.getProgress());

        if (this.spinnerVotes == null) {
            this.spinnerVotes = new Spinner<>();
            this.spinnerVotes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
        }
        this.spinnerVotes.getValueFactory().setValue(that.spinnerVotes.getValue());
    }
}
