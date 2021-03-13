package be.effectlife.arvbotplus.controllers.widgets;

import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.controllers.scenes.PollController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.Scenes;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.HashSet;
import java.util.Set;

public class PollWidgetController implements IController {
    @FXML
    private ProgressBar pBarVotes;

    @FXML
    private Spinner<Integer> spinnerVotes;

    @FXML
    private Button btnClear;

    @FXML
    private TextArea taOption;
    private PollController pollController;
    private Set<String> voters;
    private int optionId;

    @FXML
    void btnClear_Clicked(ActionEvent event) {
        spinnerVotes.getValueFactory().setValue(0);
        pBarVotes.setProgress(0);
        taOption.setText("");
    }


    @Override
    public void doInit() {
        pollController = (PollController) AESceneLoader.getInstance().getController(Scenes.S_POLL);
        spinnerVotes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
        voters = new HashSet<>();
        btnClear_Clicked(null);
    }

    @Override
    public void onShow() {

    }

    @Override
    public void reloadView() {
        spinnerVotes.getValueFactory().setValue(voters.size());
        int totalVotes = pollController.getHighestAmountOfVotes();
        if(voters.size()==0){
            pBarVotes.setProgress(0);
        }else{
            pBarVotes.setProgress((double)voters.size()/totalVotes);
        }
    }

    public void clear() {
        btnClear_Clicked(null);
    }

    public String getOptionText() {
        return taOption.getText();
    }

    public void disableButtons(boolean disabled) {
        taOption.setDisable(disabled);
        btnClear.setDisable(disabled);
    }

    public int getOptionId() {
        return optionId;
    }

    public void setOptionId(int optionId) {
        this.optionId = optionId;
    }

    public Set<String> getVoters() {
        return voters;
    }

    public void vote(String sender) {
        voters.add(sender);
    }
}
