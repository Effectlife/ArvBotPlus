package be.effectlife.arvbotplus.controllers.widgets;

import be.effectlife.arvbotplus.Main;
import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.controllers.scenes.PollController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.Scenes;
import be.effectlife.arvbotplus.utilities.PollType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class QuickPollWidgetController implements IController {
    private final Logger LOG = LoggerFactory.getLogger(QuickPollWidgetController.class);
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

    private int totalVotes, votes1, votes2;
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
        btnQPOpenClose.setText("Open");
        totalVotes = votes1 = votes2 = 0;
        if (pollController == null) {
            pollController = (PollController) AESceneLoader.getInstance().getController(Scenes.S_POLL);
        }
    }

    @Override
    public void onShow() {
    }

    @FXML
    void btnQPOpenClose_clicked(ActionEvent event) {
        if (pollController.getPollType() == PollType.NONE) {
            //Starting the poll
            btnQPOpenClose.setText("Close");
            pollController.setPollType(PollType.QUICK);
        } else if (pollController.getPollType() == PollType.QUICK) {
            //Stopping the poll
            if (votes1 == votes2) {
                //draw
                Main.twirkSystem.channelMessage("Poll closed; A draw has occurred with " + votes1 + " votes.");
            } else if (votes1 > votes2) {
                Main.twirkSystem.channelMessage("Poll closed; Option A has won with " + votes1 + " votes");
            } else {
                Main.twirkSystem.channelMessage("Poll closed; Option B has won with " + votes2 + " votes");
            }
            btnQPOpenClose.setText("Clear");
            pollController.setPollType(PollType.QP_CLEAR);
        } else if (pollController.getPollType() == PollType.QP_CLEAR) {
            //Clearing data
            pollController.setPollType(PollType.NONE);
            doInit();
        } else {
            LOG.warn("Trying to change quickpoll state, while the polltype is " + pollController.getPollType() + "... Which is strange, since the button should be disabled");
        }

    }

    @FXML
    void btnQPLastCall_clicked(ActionEvent event) {
        Main.twirkSystem.channelMessage("Last Call!");

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
        if (votes1 == 0) {
            pBar1.setProgress(0);
        } else {
            pBar1.setProgress((double) totalVotes / votes1);
        }
        if (votes2 == 0) {
            pBar2.setProgress(0);
        } else {
            pBar2.setProgress((double) totalVotes / votes2);
        }
    }

    public int castVote(int option, String sender) {

        if (option == 1) {
            if (hasUsernameVoted(sender)) {
                return 1;
            }
            usernamesVoted1.add(sender);
            totalVotes++;
            votes1++;
        } else if (option == 2) {
            if (hasUsernameVoted(sender)) {
                return 1;
            }
            usernamesVoted2.add(sender);
            totalVotes++;
            votes2++;
        } else {
            return 2;
        }
        reloadView();
        return 0;
    }

    public int changeVote(int option, String sender) {
        if (option == 2 && usernamesVoted1.contains(sender)) {
            votes1--;
            totalVotes--;
            usernamesVoted1.remove(sender);
            return castVote(option, sender);
        }
        if (option == 1 && usernamesVoted2.contains(sender)) {
            votes2--;
            totalVotes--;
            usernamesVoted2.remove(sender);
            return castVote(option, sender);
        }
        return 3;
    }

    private boolean hasUsernameVoted(String sender) {
        return usernamesVoted1.contains(sender) || usernamesVoted2.contains(sender);
    }


}
