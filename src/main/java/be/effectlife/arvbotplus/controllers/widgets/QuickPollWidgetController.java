package be.effectlife.arvbotplus.controllers.widgets;

import be.effectlife.arvbotplus.Main;
import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.controllers.scenes.PollController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.MessageKey;
import be.effectlife.arvbotplus.loading.MessageProperties;
import be.effectlife.arvbotplus.loading.Scenes;
import be.effectlife.arvbotplus.models.twirkcommands.VoteActionResult;
import be.effectlife.arvbotplus.utilities.PollType;
import be.effectlife.arvbotplus.utilities.SimplePopup;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class QuickPollWidgetController implements IController {
    private static final Logger LOG = LoggerFactory.getLogger(QuickPollWidgetController.class);
    @FXML
    private Button btnQPOpenClose;

    @FXML
    private Button btnQPLastCall;

    @FXML
    private ProgressBar pBar1;

    @FXML
    private ProgressBar pBar2;

    @FXML
    private Text textCount1;

    @FXML
    private Text textCount2;

    private int votes1;
    private int votes2;
    private Set<String> usernamesVoted1;
    private Set<String> usernamesVoted2;

    private PollController pollController;

    @Override
    public void doInit() {
        textCount1.setText("0");
        textCount2.setText("0");
        pBar1.setProgress(0);
        pBar2.setProgress(0);
        usernamesVoted1 = new HashSet<>();
        usernamesVoted2 = new HashSet<>();
        btnQPOpenClose.setText(MessageProperties.getString(MessageKey.WIDGET_QUICKPOLL_BUTTON_OPEN));
        btnQPLastCall.setText(MessageProperties.getString(MessageKey.WIDGET_QUICKPOLL_BUTTON_LASTCALL));
        if (pollController == null) {
            pollController = (PollController) AESceneLoader.getInstance().getController(Scenes.S_POLL);
        }
        votes1 = 0;
        votes2 = 0;
    }

    @FXML
    public void btnQPOpenCloseClicked(ActionEvent event) {
        if (pollController.getPollType() == PollType.NONE) {
            //Starting the poll
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_QUICKPOLL_TEMPLATE_QUESTION, new HashMap<>()));
            doInit();
            btnQPOpenClose.setText(MessageProperties.getString(MessageKey.WIDGET_QUICKPOLL_BUTTON_CLOSE));
            pollController.setPollType(PollType.QUICK);
        } else if (pollController.getPollType() == PollType.QUICK) {
            Map<String, String> params = new HashMap<>();
            params.put("votecount", (Math.max(votes1, votes2)) + "");
            //Stopping the poll
            if (votes1 == votes2) {
                //draw
                channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_QUICKPOLL_TEMPLATE_DRAW, params));
            } else if (votes1 > votes2) {
                params.put("option", "1");
                channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_QUICKPOLL_TEMPLATE_WIN, params));
            } else {
                params.put("option", "2");
                channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_QUICKPOLL_TEMPLATE_WIN, params));
            }
            btnQPOpenClose.setText(MessageProperties.getString(MessageKey.WIDGET_QUICKPOLL_BUTTON_CLEAR));
            pollController.setPollType(PollType.QP_CLEAR);
        } else if (pollController.getPollType() == PollType.QP_CLEAR) {
            //Clearing data
            pollController.setPollType(PollType.NONE);
            doInit();
        } else {
            LOG.warn("Trying to change quickpoll state, while the polltype is {}... Which is strange, since the button should be disabled", pollController.getPollType());
        }

    }

    @FXML
    void btnQPLastCallClicked(ActionEvent event) {
        channelMessage(MessageProperties.getString(MessageKey.TWIRK_MESSAGE_POLL_LASTCALL));

    }

    public void disableBtnQPOpenClose(boolean disabled) {
        btnQPOpenClose.setDisable(disabled);
    }

    public void disableBtnQPLastCall(boolean disabled) {
        btnQPLastCall.setDisable(disabled);
    }

    @Override
    public void reloadView() {
        textCount1.setText("" + votes1);
        textCount2.setText("" + votes2);
        int max = Math.max(votes1, votes2);
        if (votes1 == 0 || max == 0) {
            pBar1.setProgress(0);
        } else {
            pBar1.setProgress((double) votes1 / max);
        }
        if (votes2 == 0 || max == 0) {
            pBar2.setProgress(0);
        } else {
            pBar2.setProgress((double) votes2 / max);
        }
    }

    public VoteActionResult castVote(int option, String sender) {

        if (option == 1) {
            if (hasUsernameVoted(sender)) {
                return VoteActionResult.VOTE_ALREADY_CAST;
            }
            usernamesVoted1.add(sender);
            votes1++;
        } else if (option == 2) {
            if (hasUsernameVoted(sender)) {
                return VoteActionResult.VOTE_ALREADY_CAST;
            }
            usernamesVoted2.add(sender);
            votes2++;
        } else {
            return VoteActionResult.INVALID_VOTE;
        }
        reloadView();
        return VoteActionResult.SUCCESSFULLY_VOTED_OR_CHANGED;
    }

    public VoteActionResult changeVote(int option, String sender) {
        if (option == 2 && usernamesVoted1.contains(sender)) {
            votes1--;
            usernamesVoted1.remove(sender);
            return castVote(option, sender);
        }
        if (option == 1 && usernamesVoted2.contains(sender)) {
            votes2--;
            usernamesVoted2.remove(sender);
            return castVote(option, sender);
        }
        return VoteActionResult.ALREADY_VOTED_FOR_OPTION;
    }

    private boolean hasUsernameVoted(String sender) {
        return usernamesVoted1.contains(sender) || usernamesVoted2.contains(sender);
    }


    public void clear() {
        pollController.setPollType(PollType.NONE);
        doInit();
    }

    private void channelMessage(String message) {
        if (Main.getTwirkService() == null) {
            LOG.trace(message);
        } else {
            Main.getTwirkService().channelMessage(message);
        }
    }

    public void pBar1VotesClicked(MouseEvent mouseEvent) {
        if (!usernamesVoted1.isEmpty()) {
            String reduce = usernamesVoted1.stream().sorted().reduce("", (id, voter) -> id += voter + "\n");
            Platform.runLater(() -> SimplePopup.showPopupInfo("Voters for option 1", reduce));
        }
    }
    public void pBar2VotesClicked(MouseEvent mouseEvent) {
        if (!usernamesVoted2.isEmpty()) {
            String reduce = usernamesVoted2.stream().sorted().reduce("", (id, voter) -> id += voter + "\n");
            Platform.runLater(() -> SimplePopup.showPopupInfo("Voters for option 2", reduce));
        }
    }
}
