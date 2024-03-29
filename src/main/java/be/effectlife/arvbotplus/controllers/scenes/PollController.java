package be.effectlife.arvbotplus.controllers.scenes;

import be.effectlife.arvbotplus.Main;
import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.controllers.widgets.PollWidgetController;
import be.effectlife.arvbotplus.controllers.widgets.QuickPollWidgetController;
import be.effectlife.arvbotplus.loading.*;
import be.effectlife.arvbotplus.models.twirkcommands.VoteActionResult;
import be.effectlife.arvbotplus.saves.SaveManager;
import be.effectlife.arvbotplus.saves.models.PollOption;
import be.effectlife.arvbotplus.saves.models.PollSave;
import be.effectlife.arvbotplus.utilities.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PollController implements IController {
    private static final Logger LOG = LoggerFactory.getLogger(PollController.class);
    private PollType pollType = PollType.NONE;
    private QuickPollWidgetController quickPollWidget;
    private int options;
    private List<PollWidgetController> pollWidgetControllerList;
    private String highlight;
    private static String hoverSide = "";
    private static String previousHoverSide = "";
    private static final String BOTTOM = "paneHoverBottom";
    private static final String TOP = "paneHoverTop";
    @FXML
    private GridPane gpBase;

    @FXML
    private Button btnClearAll;

    @FXML
    private TextField taQuestion;

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
    private Text textConnectedTo;
    @FXML
    private Text textOptions;

    @FXML
    private Button btnSavePoll;

    @FXML
    private Button btnLoadPoll;

    @FXML
    void btnClearAllClicked(ActionEvent event) {
        boolean hasNotCleared = isVotesCleared();
        for (PollWidgetController pollWidgetController : pollWidgetControllerList) {
            pollWidgetController.clear(hasNotCleared);
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
    void btnLastCallClicked(ActionEvent event) {
        channelMessage(MessageProperties.getString(MessageKey.TWIRK_MESSAGE_POLL_LASTCALL));
    }

    @FXML
    void btnSavePollClicked(ActionEvent event) {
        SaveManager.savePoll();
    }

    @FXML
    void btnLoadPollClicked(ActionEvent event) {
        SaveManager.loadPoll();
    }

    @FXML
    public void btnOpenClosePollClicked(ActionEvent event) {
        if (getPollType() == PollType.NONE || getPollType() == PollType.QP_CLEAR) {
            handleNoneQPClearPoll();
        } else if (getPollType() == PollType.STANDARD) {
            handleStandardPoll();
        } else {
            LOG.error("Open Close Poll clicked when polltype was {}. Only NONE and STANDARD should be clickable", getPollType());
        }
    }

    private void handleStandardPoll() {
        List<PollWidgetController> pollWidgetWithHighestVotes = getPollWidgetWithHighestVotes();
        if (pollWidgetWithHighestVotes.size() > 1) {
            //draw
            Map<String, String> params = new HashMap<>();

            StringBuilder optionsBuilder = new StringBuilder();
            for (int i = 0; i < pollWidgetWithHighestVotes.size(); i++) {
                PollWidgetController pwc = pollWidgetWithHighestVotes.get(i);
                optionsBuilder.append("{").append(pwc.getOptionText()).append("}");
                if (i != pollWidgetWithHighestVotes.size() - 1) optionsBuilder.append(", ");
            }

            params.put("votecount", pollWidgetWithHighestVotes.size() + "");
            params.put("options", optionsBuilder.toString());

            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_POLL_TEMPLATE_DRAW, params));
        } else {
            Map<String, String> params = new HashMap<>();
            params.put("votecount", pollWidgetWithHighestVotes.get(0).getVotes() + "");
            params.put("option", (pollWidgetWithHighestVotes.get(0).getOptionId() + 1) + ": {" + pollWidgetWithHighestVotes.get(0).getOptionText() + "}");

            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_POLL_TEMPLATE_WIN, params));
        }
        btnOpenClosePoll.setText("Open");
        setPollType(PollType.NONE);
    }

    private void handleNoneQPClearPoll() {
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
        //Preparing state
        btnOpenClosePoll.setText(MessageProperties.getString(MessageKey.SCENE_POLLS_BUTTON_CLOSE));
        setPollType(PollType.STANDARD);

        //Combine them into a concise message and send it

        String question;
        Map<String, String> params = new HashMap<>();
        if (taQuestion.getText().trim().isEmpty()) {
            question = MessageProperties.getString(MessageKey.SCENE_POLLS_DEFAULTQUESTION);
        } else {
            question = taQuestion.getText().trim();
        }

        StringBuilder sbOptions = new StringBuilder();
        validOptions.forEach((key, value) -> sbOptions.append(key + 1).append(":{").append(value).append("}; "));

        params.put("question", question);
        params.put("options", sbOptions.toString());
        channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_POLL_TEMPLATE_QUESTION, params));
    }

    @FXML
    void tfOptionsAction(ActionEvent event) {
        LOG.info("Re-initializing options list");
        options = Integer.parseInt(tfOptions.getText());
        initializePollWidgets();
    }

    @Override
    public void doInit() {
        highlight = ColorHelper.retrieveColor(ColorType.HIGHLIGHT);
        SceneContainer quickPollContainer = AESceneLoader.getInstance().getSceneContainer(Scenes.W_QUICKPOLL);
        Region quickPollRegion = (Region) quickPollContainer.getScene().getRoot();
        setDefaultSize(quickPollRegion);
        gpBase.add(quickPollRegion, 0, 1, 2, 1);
        this.quickPollWidget = (QuickPollWidgetController) quickPollContainer.getController();
        options = 8;
        tfOptions.setTextFormatter(Formatters.NumbersOnly);
        tfOptions.focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal) PollController.this.tfOptionsAction(null);
                }
        );
        textConnectedTo.setText(MessageProperties.getString(MessageKey.SCENE_POLLS_TEXT_CONNECTEDTO));
        textOptions.setText(MessageProperties.getString(MessageKey.SCENE_POLLS_TEXT_OPTIONS));
        taQuestion.setPromptText(MessageProperties.getString(MessageKey.SCENE_POLLS_DEFAULTQUESTION));
        btnOpenClosePoll.setText(MessageProperties.getString(MessageKey.SCENE_POLLS_BUTTON_OPEN));
        btnLastCall.setText(MessageProperties.getString(MessageKey.SCENE_POLLS_BUTTON_LASTCALL));
        btnClearAll.setText(MessageProperties.getString(MessageKey.SCENE_POLLS_BUTTON_CLEARALL));
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
            addWithDragging(vboxPollOptions, sceneContainer.getScene().getRoot());
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
                disableButtons(false, true, false, true, false, false, false);
                break;
            case QUICK:
                disableButtons(false, false, true, true, true, false, false);
                break;
            case STANDARD:
                disableButtons(true, true, false, false, true, true, true);
                break;
            default:
                disableButtons(false, false, false, false, false, false, false);
                LOG.error("Unknown polltype was set: {}", pollType);
        }
        this.pollType = pollType;
    }

    private void disableButtons(boolean btnQPOpenClose, boolean btnQPLastCall, boolean openClose, boolean lastCall, boolean clearAll, boolean options, boolean load) {
        quickPollWidget.disableBtnQPOpenClose(btnQPOpenClose);
        quickPollWidget.disableBtnQPLastCall(btnQPLastCall);
        this.btnOpenClosePoll.setDisable(openClose);
        this.btnLastCall.setDisable(lastCall);
        this.btnClearAll.setDisable(clearAll);
        this.tfOptions.setDisable(options);
        this.btnLoadPoll.setDisable(load);
        if (pollWidgetControllerList != null) {
            for (PollWidgetController pollWidgetController : pollWidgetControllerList) {
                pollWidgetController.disableButtons(options);
            }
        }
    }

    public void onShow() {
        textConnection.setText(Main.getTwirkService().getConnectedChannel());
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
                return VoteActionResult.VOTE_ALREADY_CAST;

            }
        }
        return castVoteAnyway(option, sender);
    }

    public VoteActionResult castVoteAnyway(int option, String sender) {
        for (PollWidgetController pollWidgetController : pollWidgetControllerList) {
            if (pollWidgetController.getOptionId() == option && StringUtils.isNotBlank(pollWidgetController.getOptionText())) {
                pollWidgetController.vote(sender);
                return VoteActionResult.SUCCESSFULLY_VOTED_OR_CHANGED;
            }
        }
        //If this is reached, the option was not found in the widgets, so the vote was invalid
        return VoteActionResult.INVALID_VOTE;
    }

    public int getHighestAmountOfVotes() {

        int votes = 0;
        List<PollWidgetController> pollWidgetWithHighestVotes = getPollWidgetWithHighestVotes();
        if (!pollWidgetWithHighestVotes.isEmpty()) {
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
        PollWidgetController controllerForSender = getControllerForSendersVote(sender);
        if (controllerForSender == null) {
            return VoteActionResult.NOT_YET_VOTED;
        } else if (controllerForSender.getOptionId() == option) {
            return VoteActionResult.ALREADY_VOTED_FOR_OPTION;
        }

        VoteActionResult voteActionResult = castVoteAnyway(option, sender);
        if (voteActionResult == VoteActionResult.SUCCESSFULLY_VOTED_OR_CHANGED) {
            controllerForSender.removeVote(sender);
            reloadView();
        }
        return voteActionResult;

    }

    private PollWidgetController getControllerForSendersVote(String sender) {
        for (PollWidgetController pollWidgetController : pollWidgetControllerList) {
            if (pollWidgetController.getVoters().contains(sender)) return pollWidgetController;
        }
        return null;
    }

    private void channelMessage(String message) {
        if (Main.getTwirkService() == null) {
            LOG.trace(message);
        } else {
            Main.getTwirkService().channelMessage(message);
        }
    }

    public String getOptionText(int option) {
        for (PollWidgetController pollWidgetController : pollWidgetControllerList) {
            if (pollWidgetController.getOptionId() == option)
                return (pollWidgetController.getOptionId() + 1) + ": {" + pollWidgetController.getOptionText() + "}";
        }
        return null;
    }

    public int getOptionCount() {
        return options;
    }

    public String getQuestionText() {
        return taQuestion.getText().trim();
    }

    public List<PollOption> getOptions() {
        List<PollOption> optionList = new ArrayList<>();
        for (int i = 0; i < getOptionCount(); i++) {
            PollWidgetController optionController = pollWidgetControllerList.get(i);
            optionList.add(new PollOption(optionController.getOptionText(), optionController.getVotes(), new ArrayList<>(optionController.getVoters())));
        }
        return optionList;
    }

    public void load(List<String> inputList) {
        btnClearAllClicked(null);
        btnClearAllClicked(null);
        tfOptions.setText(inputList.size() + "");
        tfOptionsAction(null);
        for (int i = 0; i < inputList.size(); i++) {
            PollWidgetController optionController = pollWidgetControllerList.get(i);
            optionController.setOptionText(inputList.get(i));
        }
    }

    public void load(PollSave pollSave) {
        try {
            btnClearAllClicked(null);
            btnClearAllClicked(null);
            tfOptions.setText(pollSave.getOptionCount() + "");
            tfOptionsAction(null);
            for (int i = 0; i < pollSave.getOptions().size(); i++) {
                PollOption pollOption = pollSave.getOptions().get(i);
                PollWidgetController optionController = pollWidgetControllerList.get(i);
                optionController.setOptionText(pollOption.getOptionText());
                optionController.setVoters(pollOption.getVoters());
                optionController.setVotes(pollOption.getVotes());
            }
            taQuestion.setText(pollSave.getQuestion());
        } catch (Exception e) {
            SimplePopup.showPopupError("File cannot be loaded, are you sure it is a Polls save?\n\nException: " + e.getMessage() + "\n" + e.getStackTrace()[0]);
            LOG.error("File cannot be loaded", e);
        }
    }


    private void addWithDragging(final VBox root, final Parent node) {
        node.setOnDragDetected(event -> node.startFullDrag());
        node.setOnMouseDragOver(event -> {
            String id = ((Node) event.getTarget()).getId();
            if (id.equals(BOTTOM)) hoverSide = BOTTOM;
            else if (id.equals(TOP)) hoverSide = TOP;

            if (!previousHoverSide.equals(hoverSide)) {
                previousHoverSide = hoverSide;
                if (hoverSide.equals(TOP)) {
                    node.setStyle("-fx-border-color: " + highlight + " transparent transparent transparent; -fx-border-width: 1 0 1 0;");
                } else if (hoverSide.equals(BOTTOM)) {
                    node.setStyle("-fx-border-color: transparent transparent" + highlight + " transparent; -fx-border-width: 1 0 1 0;");
                }
            }

        });
        // next two handlers just an idea how to show the drop target visually:
        node.setOnMouseDragEntered(event -> {
            //stackPane is the stackPane, obviously
            node.getChildrenUnmodifiable().stream().filter(node1 -> node1.getId().equals("stackPane")).findFirst().flatMap(stackPane -> ((Parent) stackPane).getChildrenUnmodifiable().stream().filter(node1 -> node1.getId().equals("gpHover")).findFirst()).ifPresent((hoverNode -> {    //Running through the children of the stackpane, and only selecting the gpHover one. Needs to be enabled here
                hoverNode.setDisable(false);
                hoverNode.setVisible(true);
            }));

        });
        node.setOnMouseDragExited(event -> {
            node.getChildrenUnmodifiable().stream().filter(node1 -> node1.getId().equals("stackPane")).findFirst().flatMap(stackPane -> ((Parent) stackPane).getChildrenUnmodifiable().stream().filter(node1 -> node1.getId().equals("gpHover")).findFirst()).ifPresent((hoverNode -> {    //Running through the children of the stackpane, and only selecting the gpHover one. Needs to be enabled here
                hoverNode.setDisable(true);
                hoverNode.setVisible(false);
            }));
            node.setStyle("-fx-border-color: transparent; -fx-border-width: 1 0 1 0;");
        });

        node.setOnMouseDragReleased(event -> {
            node.setStyle("");
            int indexOfDraggingNode = root.getChildren().indexOf(event.getGestureSource());
            int indexOfDropTarget = root.getChildren().indexOf(node);
            if (hoverSide.equals(TOP)) {
                indexOfDropTarget--;
            }
            if (indexOfDropTarget < indexOfDraggingNode) indexOfDropTarget++;
            rotateNodes(indexOfDraggingNode, indexOfDropTarget);
            event.consume();
        });
        root.getChildren().add(node);
    }

    private void rotateNodes(final int indexOfDraggingNode,
                             final int indexOfDropTarget) {
        if (indexOfDraggingNode >= 0 && indexOfDropTarget >= 0) {
            PollWidgetController tempController = new PollWidgetController();
            PollWidgetController draggingController = pollWidgetControllerList.get(indexOfDraggingNode);
            tempController.copyData(draggingController);

            if (indexOfDraggingNode > indexOfDropTarget) {
                //Dragging up
                LOG.info("Rotating up, " + indexOfDraggingNode + "->" + indexOfDropTarget);
                //Save Dragging node into temp, move others each down 1 until target is moved, then copy temp into target
                for (int i = indexOfDraggingNode - 1; i >= indexOfDropTarget; i--) {
                    pollWidgetControllerList.get(i + 1).copyData(pollWidgetControllerList.get(i));
                }
                pollWidgetControllerList.get(indexOfDropTarget).copyData(tempController);

            } else if (indexOfDraggingNode < indexOfDropTarget) {
                //Dragging down
                LOG.info("Rotating down, " + indexOfDraggingNode + "->" + indexOfDropTarget);
                for (int i = indexOfDraggingNode; i < indexOfDropTarget; i++) {
                    pollWidgetControllerList.get(i).copyData(pollWidgetControllerList.get(i + 1));
                }
                pollWidgetControllerList.get(indexOfDropTarget).copyData(tempController);
            }

            reloadView();
        }
    }
}
