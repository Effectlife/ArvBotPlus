package be.effectlife.arvbotplus.controllers.scenes;

import be.effectlife.arvbotplus.Main;
import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.controllers.widgets.PollWidgetController;
import be.effectlife.arvbotplus.controllers.widgets.QuickPollWidgetController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.SceneContainer;
import be.effectlife.arvbotplus.loading.Scenes;
import be.effectlife.arvbotplus.twirk.commands.VoteCommand;
import be.effectlife.arvbotplus.utilities.Formatters;
import be.effectlife.arvbotplus.utilities.PollType;
import be.effectlife.arvbotplus.utilities.SimplePopup;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PollController implements IController {
    Logger LOG = LoggerFactory.getLogger(PollController.class);
    private PollType pollType = PollType.NONE;
    private QuickPollWidgetController quickPollWidget;
    private int options;
    private List<PollWidgetController> pollWidgetControllerList;

    @FXML
    private GridPane gpBase;

    @FXML
    private Button btnClearAll;

    @FXML
    private TextArea taQuestion;

    @FXML
    private TextField tfOptions;

    @FXML
    private Button btnOpenClosePoll;

    @FXML
    private Button btnLastCall;

    @FXML
    private VBox vboxPollOptions;

    @FXML
    void btnClearAll_Clicked(ActionEvent event) {
        for (PollWidgetController pollWidgetController : pollWidgetControllerList) {
            pollWidgetController.clear();
        }
    }

    @FXML
    void btnLastCall_Clicked(ActionEvent event) {
        Main.twirkSystem.channelMessage("Last Call!");
    }

    @FXML
    void btnOpenClosePoll_Clicked(ActionEvent event) {
        if (getPollType() == PollType.NONE) {


            Map<Integer, String> validOptions = new HashMap<>();
            //Collect all id's and options that are not empty
            for (int i = 0; i < pollWidgetControllerList.size(); i++) {
                if (!pollWidgetControllerList.get(i).getOptionText().isEmpty()) {
                    validOptions.put(i, pollWidgetControllerList.get(i).getOptionText());
                }
            }
            if (validOptions.size() == 0) {
                SimplePopup.showPopupWarn("You haven't entered any options.");
                return;
            } else if (validOptions.size() == 1) {
                SimplePopup.showPopupWarn("You have entered only 1 option.");
                return;
            }
            btnOpenClosePoll.setText("Close");
            setPollType(PollType.STANDARD);
            //Combine them into a concise message and send it
            StringBuilder stringBuilder = new StringBuilder();
            if (taQuestion.getText().trim().isEmpty()) {
                stringBuilder.append("What should we do/say? | ");
            } else {
                stringBuilder.append(taQuestion.getText().trim() + " | ");
            }
            stringBuilder.append("Vote using " + VoteCommand.PATTERN + " {option} for: ");
            validOptions.forEach((key, value) -> {
                stringBuilder.append((key + 1) + ":{" + value + "}; ");
            });
            Main.twirkSystem.channelMessage(stringBuilder.toString());
        } else if (getPollType() == PollType.STANDARD) {
            btnOpenClosePoll.setText("Open");
            setPollType(PollType.NONE);
        } else {
            LOG.error("Open Close Poll clicked when polltype was " + getPollType() + ". Only NONE and STANDARD should be clickable");
        }
    }

    @FXML
    void tfOptions_Action(ActionEvent event) {
        LOG.info("Re-initializing options list");
        options = Integer.parseInt(tfOptions.getText());
        initializePollWidgets();
    }

    @Override
    public void doInit() {
        SceneContainer quickPollContainer = AESceneLoader.getInstance().getSceneContainer(Scenes.W_QUICKPOLL);
        Region region = (Region) quickPollContainer.getScene().getRoot();
        setDefaultSize(region);
        gpBase.add(region, 0, 0);
        this.quickPollWidget = (QuickPollWidgetController) quickPollContainer.getController();
        //TODO: Add loading from save
        options = 8;
        tfOptions.setTextFormatter(Formatters.NumbersOnly);
        tfOptions.focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal) PollController.this.tfOptions_Action(null);
                }
        );
        taQuestion.setText("");
        tfOptions.setText(options + "");
        setPollType(PollType.NONE);
        pollWidgetControllerList = new ArrayList<>();
        initializePollWidgets();
    }

    private void initializePollWidgets() {
        pollWidgetControllerList.clear();
        vboxPollOptions.getChildren().clear();
        for (int i = 0; i < options; i++) {
            SceneContainer sceneContainer = AESceneLoader.getInstance().getSceneContainer(Scenes.W_POLL, "_" + i);
            vboxPollOptions.getChildren().add(sceneContainer.getScene().getRoot());
            setDefaultSize((Region) sceneContainer.getScene().getRoot());
            pollWidgetControllerList.add((PollWidgetController) sceneContainer.getController());
            ((PollWidgetController) sceneContainer.getController()).setOptionId(i);
        }
    }

    public PollType getPollType() {
        return pollType;
    }

    public void setPollType(PollType pollType) {
        switch (pollType) {
            case NONE:
            case QP_CLEAR:
                updateButtons(false, true, false, true, false, false);
                break;
            case QUICK:
                updateButtons(false, false, true, true, true, false);
                break;
            case STANDARD:
                updateButtons(true, true, false, false, true, true);
                break;
            default:
                updateButtons(false, false, false, false, false, false);
                LOG.error("Unknown polltype was set: " + pollType);
        }
        this.pollType = pollType;
    }

    private void updateButtons(boolean btnQPOpenClose, boolean btnQPLastCall, boolean openClose, boolean lastCall, boolean clearAll, boolean options) {
        quickPollWidget.disableBtnQPOpenClose(btnQPOpenClose);
        quickPollWidget.disableBtnQPLastCall(btnQPLastCall);
        this.btnOpenClosePoll.setDisable(openClose);
        this.btnLastCall.setDisable(lastCall);
        this.btnClearAll.setDisable(clearAll);
        this.tfOptions.setDisable(options);
        if (pollWidgetControllerList != null) {
            for (PollWidgetController pollWidgetController : pollWidgetControllerList) {
                pollWidgetController.disableButtons(options);
            }
        }
    }

    @Override
    public void onShow() {

    }

    @Override
    public void reloadView() {
        pollWidgetControllerList.forEach(PollWidgetController::reloadView);
    }

    /**
     * @param option
     * @param sender
     * @return 0 for success;<br>
     * 1 for already voted;<br>
     * 2 for illegal vote
     */
    public int castVote(int option, String sender) {
        for (PollWidgetController pollWidgetController : pollWidgetControllerList) {
            //check for already voted
            if (pollWidgetController.getVoters().contains(sender)) {
                return 1;

            }
        }
        for (PollWidgetController pollWidgetController : pollWidgetControllerList) {
            if (pollWidgetController.getOptionId() == option) {
                pollWidgetController.vote(sender);
                return 0;
            }
        }
        return 2;
    }

    public int getHighestAmountOfVotes() {
        int highest = 0;
        for (PollWidgetController pollWidgetController : pollWidgetControllerList) {
            if (pollWidgetController.getVoters().size() > highest) {
                highest = pollWidgetController.getVoters().size();
            }
        }
        return highest;
    }
}
