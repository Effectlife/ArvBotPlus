package be.effectlife.arvbotplus.controllers.scenes;

import be.effectlife.arvbotplus.Main;
import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.controllers.widgets.PollWidgetController;
import be.effectlife.arvbotplus.controllers.widgets.QuickPollWidgetController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.SceneContainer;
import be.effectlife.arvbotplus.loading.Scenes;
import be.effectlife.arvbotplus.twirk.commands.VoteActionResult;
import be.effectlife.arvbotplus.twirk.commands.VoteCommand;
import be.effectlife.arvbotplus.utilities.Formatters;
import be.effectlife.arvbotplus.utilities.JFXExtensions;
import be.effectlife.arvbotplus.utilities.PollType;
import be.effectlife.arvbotplus.utilities.SimplePopup;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
    private Text textConnection;

    @FXML
    void btnClearAll_Clicked(ActionEvent event) {
        for (PollWidgetController pollWidgetController : pollWidgetControllerList) {
            pollWidgetController.clear(isVotesCleared());
        }
    }

    private boolean isVotesCleared() {
        for (PollWidgetController pollWidgetController : pollWidgetControllerList) {
            if (!pollWidgetController.isCleared()) {
                return false;
            }
        }
        return true;

    }

    @FXML
    void btnLastCall_Clicked(ActionEvent event) {
        Main.twirkSystem.channelMessage("Last Call!");
    }

    @FXML
    void btnOpenClosePoll_Clicked(ActionEvent event) {
        if (getPollType() == PollType.NONE || getPollType() == PollType.QP_CLEAR) {
            quickPollWidget.clear();
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
            //reset voters when starting a new poll without clearing manually
            for (PollWidgetController pollWidgetController : pollWidgetControllerList) {
                pollWidgetController.resetVotes();
            }
            //resetting quickpoll
            btnOpenClosePoll.setText("Close");
            setPollType(PollType.STANDARD);
            //Combine them into a concise message and send it
            StringBuilder stringBuilder = new StringBuilder();
            if (taQuestion.getText().trim().isEmpty()) {
                stringBuilder.append("What should we do/say? | ");
            } else {
                stringBuilder.append(taQuestion.getText().trim()).append(" | ");
            }
            stringBuilder.append("Vote using " + VoteCommand.PATTERN + " {option} for: ");
            validOptions.forEach((key, value) -> {
                stringBuilder.append(key + 1).append(":{").append(value).append("}; ");
            });
            Main.twirkSystem.channelMessage(stringBuilder.toString());
        } else if (getPollType() == PollType.STANDARD) {
            List<PollWidgetController> pollWidgetWithHighestVotes = getPollWidgetWithHighestVotes();
            if (pollWidgetWithHighestVotes.size() > 1) {
                //draw
                StringBuilder sb = new StringBuilder();
                sb.append("Poll closed; There was a draw with ").append(pollWidgetWithHighestVotes.get(0).getVotes()).append(" votes between the following options: ");
                for (int i = 0; i < pollWidgetWithHighestVotes.size(); i++) {
                    PollWidgetController pwc = pollWidgetWithHighestVotes.get(i);
                    sb.append("'").append(pwc.getOptionText()).append("'");
                    if (i != pollWidgetWithHighestVotes.size() - 1) sb.append(", ");
                }
                Main.twirkSystem.channelMessage(sb.toString());
            } else {
                Main.twirkSystem.channelMessage(String.format("Poll closed; the following option has won with %d votes: %d: %s", pollWidgetWithHighestVotes.get(0).getVotes(), pollWidgetWithHighestVotes.get(0).getOptionId() + 1, pollWidgetWithHighestVotes.get(0).getOptionText()));
            }
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
        gpBase.add(region, 0, 2);
        this.quickPollWidget = (QuickPollWidgetController) quickPollContainer.getController();
        options = 8;
        tfOptions.setTextFormatter(Formatters.NumbersOnly);
        tfOptions.focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal) PollController.this.tfOptions_Action(null);
                }
        );
        taQuestion.setText("");
        taQuestion.addEventFilter(KeyEvent.KEY_PRESSED, JFXExtensions.tabTraverse);
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
        textConnection.setText(Main.twirkSystem.getConnectedChannel());
    }

    @Override
    public void reloadView() {
        Platform.runLater(() -> pollWidgetControllerList.forEach(PollWidgetController::reloadView));
    }

    /**
     * @param option
     * @param sender
     * @return 0 for success;<br>
     * 1 for already voted;<br>
     * 2 for illegal vote
     */
    public VoteActionResult castVote(int option, String sender) {
        for (PollWidgetController pollWidgetController : pollWidgetControllerList) {
            //check for already voted
            if (pollWidgetController.getVoters().contains(sender)) {
                return VoteActionResult.ALREADY_VOTED;

            }
        }
        return castVoteAnyway(option, sender);
    }

    public VoteActionResult castVoteAnyway(int option, String sender) {
        for (PollWidgetController pollWidgetController : pollWidgetControllerList) {
            if (pollWidgetController.getOptionId() == option && StringUtils.isNotBlank(pollWidgetController.getOptionText())) {
                pollWidgetController.vote(sender);
                return VoteActionResult.ADDED;
            }
        }
        return VoteActionResult.INVALID_VOTE;
    }

    public int getHighestAmountOfVotes() {

        int votes = 0;
        List<PollWidgetController> pollWidgetWithHighestVotes = getPollWidgetWithHighestVotes();
        if (pollWidgetWithHighestVotes.size() > 0) {
            votes = pollWidgetWithHighestVotes.get(0).getVotes();
        }
        return votes;
    }

    private List<PollWidgetController> getPollWidgetWithHighestVotes() {
        List<PollWidgetController> highests = new ArrayList<>();
        int highest = 0;
        for (PollWidgetController pollWidgetController : pollWidgetControllerList) {
            if (StringUtils.isBlank(pollWidgetController.getOptionText())) continue;
            if (pollWidgetController.getVotes() > highest) {
                highest = pollWidgetController.getVotes();
                highests.clear();
                highests.add(pollWidgetController);
            } else if (pollWidgetController.getVotes() == highest) {
                highests.add(pollWidgetController);
            }
        }
        return highests;
    }

    public VoteActionResult changeVote(int option, String sender) {
        PollWidgetController controllerForSender = getControllerForSender(sender);
        if (controllerForSender == null) {
            //Voter hasnt voted yet,
            return VoteActionResult.NO_VOTE_YET;
        } else if (controllerForSender.getOptionId() == option) {
            return VoteActionResult.SAME_VOTE;
        }

        VoteActionResult voteActionResult = castVoteAnyway(option, sender);
        if (voteActionResult != VoteActionResult.INVALID_VOTE) {
            controllerForSender.removeVote(sender);
            reloadView();
        }
        return voteActionResult;

    }

    private PollWidgetController getControllerForSender(String sender) {
        for (PollWidgetController pollWidgetController : pollWidgetControllerList) {
            if (pollWidgetController.getVoters().contains(sender)) return pollWidgetController;
        }
        return null;
    }
}
