package be.effectlife.arvbotplus.controllers.widgets;

import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.controllers.scenes.PollController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.Scenes;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;

import java.util.HashSet;
import java.util.Set;

public class QuickPollWidgetController implements IController {
    @FXML
    private Button buttonQP;

    @FXML
    private ProgressBar pBar1;

    @FXML
    private ProgressBar pBar2;

    @FXML
    private Text textCount1;

    @FXML
    private Text textCount2;

    private int totalVotes = 0, votes1 = 0, votes2 = 0;
    private Set<String> usernamesVoted1;
    private Set<String> usernamesVoted2;

    @Override
    public void doInit() {
        textCount1.setText("0");
        textCount2.setText("0");
        pBar1.setProgress(0);
        pBar2.setProgress(0);
        usernamesVoted1 = new HashSet<>();
        usernamesVoted2 = new HashSet<>();
    }

    @Override
    public void onShow() {

    }

    @FXML
    void btnQP_clicked(ActionEvent event) {
        ((PollController) AESceneLoader.getInstance().getController(Scenes.S_POLL)).startStopQuickPoll();
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
