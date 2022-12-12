package be.effectlife.arvbotplus.controllers.scenes;

import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.utilities.GiveawayStatus;
import be.effectlife.arvbotplus.utilities.SimplePopup;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class GiveawayController implements IController {
    private static final Logger LOG = LoggerFactory.getLogger(GiveawayController.class);
    @FXML
    private Text txtTitle;

    @FXML
    private Text txtTimer;

    @FXML
    private Text txtStatusTitle;

    @FXML
    private Text txtStatus;

    @FXML
    private Text txtKeywordTitle;

    @FXML
    private Text txtKeyword;

    @FXML
    private Text txtEntries;

    @FXML
    private Text txtChance;

    private int timeElapsedInSeconds = 0;

    private Timer timer = new Timer();
    private String keyword;
    private final Set<String> entries;
    private GiveawayStatus giveawayStatus = GiveawayStatus.NOT_RUNNING;

    public GiveawayController() {
        entries = new HashSet<>();
    }

    @Override
    public void doInit() {
        LOG.info("Clearing/(Re)Initializing Giveways");
        entries.clear();
        giveawayStatus = GiveawayStatus.NOT_RUNNING;
        txtStatus.setText(GiveawayStatus.NOT_RUNNING.toString());
        txtStatusTitle.setText("Status:");
        txtKeyword.setText("");
        txtEntries.setText("Entries: 0");
        txtChance.setText("Chance: 00%");
        txtTimer.setText("00:00");
        timeElapsedInSeconds = 0;
        timer.cancel();
        timer = new Timer();
    }

    @Override
    public void reloadView() {
        txtStatus.setText(giveawayStatus.toString());
        txtEntries.setText("Entries: " + entries.size());
        txtChance.setText(String.format("Chance: %02d%%", entries.isEmpty() ? 0 : (100 / entries.size())));
        txtKeyword.setText(keyword.toUpperCase());
    }

    public void setClearSetKeywordAndStart(String content) {
        if (giveawayStatus == GiveawayStatus.RUNNING || giveawayStatus == GiveawayStatus.LAST_CALL) {
            LOG.info("Status = {}; No need to clear", giveawayStatus);
            return; // Is already running, no need to reset
        }
        String temp = content.replace("[GIVEAWAY] The giveaway has started! Please type ", "");
        temp = temp.substring(0, temp.indexOf(' '));
        keyword = temp.trim();
        LOG.info("Clearing, setting Keyword to {} and starting giveaway", keyword);
        doInit();
        giveawayStatus = GiveawayStatus.RUNNING;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> txtTimer.setText(LocalTime.ofSecondOfDay(timeElapsedInSeconds++).toString()));
            }
        }, 1000L, 1000L);
        reloadView();
    }

    public void registerEntry(String displayName) {
        boolean addSuccess = entries.add(displayName);
        if (addSuccess) LOG.info("Registering entry for {}", displayName);
        else LOG.info("{} was already registered for the giveaway", displayName);
        reloadView();
    }

    public GiveawayStatus getGiveawayStatus() {
        return giveawayStatus;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setLastCall() {
        giveawayStatus = GiveawayStatus.LAST_CALL;
        reloadView();
    }

    public void doWin(String content) {
        doInit();
        final String trim = content.replace("[Giveaway] ", "").trim();
        txtStatusTitle.setText("Winner!");
        txtStatus.setText(trim.substring(0, trim.indexOf(',')));
    }

    @FXML
    void txtEntriesClicked(MouseEvent event) {
        if (!entries.isEmpty()) {
            String reduce = entries.stream().sorted().reduce("", (id, voter) -> id += voter + "\n");
            Platform.runLater(() -> SimplePopup.showPopupInfo("Entries for the Giveaway", reduce));
        }
    }
}
