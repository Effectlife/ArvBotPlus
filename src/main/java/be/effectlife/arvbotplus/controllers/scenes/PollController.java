package be.effectlife.arvbotplus.controllers.scenes;

import be.effectlife.arvbotplus.Main;
import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.controllers.widgets.PollWidgetController;
import be.effectlife.arvbotplus.controllers.widgets.QuickPollWidgetController;
import be.effectlife.arvbotplus.loading.*;
import be.effectlife.arvbotplus.models.twirkcommands.NominationResult;
import be.effectlife.arvbotplus.models.twirkcommands.VoteActionResult;
import be.effectlife.arvbotplus.saves.SaveManager;
import be.effectlife.arvbotplus.saves.models.PollOption;
import be.effectlife.arvbotplus.saves.models.PollSave;
import be.effectlife.arvbotplus.utilities.*;

import java.util.*;
import java.util.stream.Collectors;

import com.gikk.twirk.types.users.TwitchUser;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PollController implements IController {
    private static final Logger LOG = LoggerFactory.getLogger(be.effectlife.arvbotplus.controllers.scenes.PollController.class);
    private PollType pollType;
    private QuickPollWidgetController quickPollWidget;
    private int options;
    private int multi;
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
    private TextField tfMulti;
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
    private Text textMulti;
    @FXML
    private Button btnSavePoll;
    @FXML
    private Button btnLoadPoll;
    @FXML
    private CheckBox cbNomination;

    public PollController() {
        this.pollType = PollType.NONE;
    }

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
        this.channelMessage(MessageProperties.getString(MessageKey.TWIRK_MESSAGE_POLL_LASTCALL));
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
        if (this.getPollType() != PollType.NONE && this.getPollType() != PollType.QP_CLEAR) {
            if (this.getPollType() == PollType.STANDARD) {
                this.handleStandardPoll();
            } else {
                LOG.error("Open Close Poll clicked when polltype was {}. Only NONE and STANDARD should be clickable", this.getPollType());
            }
        } else {
            this.handleNoneQPClearPoll();
        }

    }

    private void handleStandardPoll() {
        List<PollWidgetController> pollWidgetWithHighestVotes = this.getPollWidgetWithHighestVotes();
        if (pollWidgetWithHighestVotes.size() > 1) {
            Map<String, String> params = new HashMap();
            StringBuilder optionsBuilder = new StringBuilder();

            for (int i = 0; i < pollWidgetWithHighestVotes.size(); ++i) {
                PollWidgetController pwc = pollWidgetWithHighestVotes.get(i);
                optionsBuilder.append("{").append(pwc.getOptionText()).append("}");
                if (i != pollWidgetWithHighestVotes.size() - 1) {
                    optionsBuilder.append(", ");
                }
            }

            params.put("votecount", pollWidgetWithHighestVotes.size() + "");
            params.put("options", optionsBuilder.toString());
            this.channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_POLL_TEMPLATE_DRAW, params));
        } else {
            Map<String, String> params = new HashMap();
            params.put("votecount", pollWidgetWithHighestVotes.get(0).getVotes() + "");
            params.put("option", pollWidgetWithHighestVotes.get(0).getOptionId() + 1 + ": {" + pollWidgetWithHighestVotes.get(0).getOptionText() + "}");
            this.channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_POLL_TEMPLATE_WIN, params));
        }

        this.btnOpenClosePoll.setText("Open");
        this.setPollType(PollType.NONE);
    }

    private void handleNoneQPClearPoll() {
        this.quickPollWidget.clear();
        Map<Integer, String> validOptions = new HashMap();

        for (int i = 0; i < pollWidgetControllerList.size(); ++i) {
            if (!pollWidgetControllerList.get(i).getOptionText().isEmpty()) {
                validOptions.put(i, pollWidgetControllerList.get(i).getOptionText());
            }
        }

        if (validOptions.size() == 0) {
            SimplePopup.showPopupWarn("You haven't entered any options.");
        } else if (validOptions.size() == 1) {
            SimplePopup.showPopupWarn("You have entered only 1 option.");
        } else {
            for (PollWidgetController pollWidgetController : pollWidgetControllerList) {
                pollWidgetController.resetVotes();
            }

            this.btnOpenClosePoll.setText(MessageProperties.getString(MessageKey.SCENE_POLLS_BUTTON_CLOSE));
            this.setPollType(PollType.STANDARD);
            Map<String, String> params = new HashMap();
            String question;
            if (this.taQuestion.getText().trim().isEmpty()) {
                question = MessageProperties.getString(MessageKey.SCENE_POLLS_DEFAULTQUESTION);
            } else {
                question = this.taQuestion.getText().trim();
            }

            StringBuilder sbOptions = new StringBuilder();
            validOptions.forEach((key, value) -> sbOptions.append(key + 1).append(":{").append(value).append("}; "));
            params.put("question", question);
            params.put("options", sbOptions.toString());
            this.channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_POLL_TEMPLATE_QUESTION, params));
        }
    }

    @FXML
    void tfOptionsAction(ActionEvent event) {
        LOG.info("Re-initializing options list");
        this.options = Integer.parseInt(this.tfOptions.getText());
        this.initializePollWidgets();
    }

    @FXML
    void tfMultiAction(ActionEvent event) {
        this.multi = Integer.parseInt(this.tfMulti.getText());
    }

    public void doInit() {
        this.highlight = ColorHelper.retrieveColor(ColorType.HIGHLIGHT);
        SceneContainer quickPollContainer = AESceneLoader.getInstance().getSceneContainer(Scenes.W_QUICKPOLL);
        Region quickPollRegion = (Region) quickPollContainer.getScene().getRoot();
        this.setDefaultSize(quickPollRegion);
        this.gpBase.add(quickPollRegion, 0, 1, 2, 1);
        this.quickPollWidget = (QuickPollWidgetController) quickPollContainer.getController();
        this.options = 8;
        this.multi = 1;
        this.tfMulti.setText(String.valueOf(this.multi));
        this.tfOptions.setTextFormatter(Formatters.getNumbersOnlyFormatter());
        this.tfOptions.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                this.tfOptionsAction(null);
            }

        });
        this.tfMulti.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                this.tfMultiAction(null);
            }

        });
        this.tfMulti.setTextFormatter(Formatters.getNumbersOnlyFormatter());
        this.textConnectedTo.setText(MessageProperties.getString(MessageKey.SCENE_POLLS_TEXT_CONNECTEDTO));
        this.textOptions.setText(MessageProperties.getString(MessageKey.SCENE_POLLS_TEXT_OPTIONS));
        this.textMulti.setText(MessageProperties.getString(MessageKey.SCENE_POLLS_TEXT_MULTI));
        this.taQuestion.setPromptText(MessageProperties.getString(MessageKey.SCENE_POLLS_DEFAULTQUESTION));
        this.btnOpenClosePoll.setText(MessageProperties.getString(MessageKey.SCENE_POLLS_BUTTON_OPEN));
        this.btnLastCall.setText(MessageProperties.getString(MessageKey.SCENE_POLLS_BUTTON_LASTCALL));
        this.btnClearAll.setText(MessageProperties.getString(MessageKey.SCENE_POLLS_BUTTON_CLEARALL));
        this.taQuestion.setText("");
        this.taQuestion.addEventFilter(KeyEvent.KEY_PRESSED, JFXExtensions.tabTraverse);
        this.tfOptions.setText(String.valueOf(this.options));
        this.setPollType(PollType.NONE);
        pollWidgetControllerList = new ArrayList();
        this.initializePollWidgets();
    }

    private void initializePollWidgets() {
        pollWidgetControllerList.clear();
        this.vboxPollOptions.getChildren().clear();

        for (int i = 0; i < this.options; ++i) {
            SceneContainer sceneContainer = AESceneLoader.getInstance().getSceneContainer(Scenes.W_POLL, "_" + i);
            this.addWithDragging(this.vboxPollOptions, sceneContainer.getScene().getRoot());
            this.setDefaultSize((Region) sceneContainer.getScene().getRoot());
            pollWidgetControllerList.add((PollWidgetController) sceneContainer.getController());
            ((PollWidgetController) sceneContainer.getController()).setOptionId(i);
        }

    }

    public PollType getPollType() {
        return this.pollType;
    }

    public void setPollType(PollType pollType) {
        switch (pollType) {
            case NONE:
            case QP_CLEAR:
                this.disableButtons(false, true, false, true, false, false, false);
                break;
            case QUICK:
                this.disableButtons(false, false, true, true, true, false, false);
                break;
            case STANDARD:
                this.disableButtons(true, true, false, false, true, true, true);
                break;
            default:
                this.disableButtons(false, false, false, false, false, false, false);
                LOG.error("Unknown polltype was set: {}", pollType);
        }

        this.pollType = pollType;
    }

    private void disableButtons(boolean btnQPOpenClose, boolean btnQPLastCall, boolean openClose, boolean lastCall, boolean clearAll, boolean options, boolean load) {
        this.quickPollWidget.disableBtnQPOpenClose(btnQPOpenClose);
        this.quickPollWidget.disableBtnQPLastCall(btnQPLastCall);
        this.btnOpenClosePoll.setDisable(openClose);
        this.btnLastCall.setDisable(lastCall);
        this.btnClearAll.setDisable(clearAll);
        this.tfOptions.setDisable(options);
        this.cbNomination.setDisable(options);
        this.btnLoadPoll.setDisable(load);
        if (pollWidgetControllerList != null) {
            for (PollWidgetController pollWidgetController : pollWidgetControllerList) {
                pollWidgetController.disableButtons(options);
            }
        }
        if(options) {
            cbNomination.setSelected(false);
        }

    }

    public void onShow() {
        this.textConnection.setText(Main.getTwirkService().getConnectedChannel());
    }

    public void reloadView() {
        Platform.runLater(() -> pollWidgetControllerList.forEach(PollWidgetController::reloadView));
    }

    public VoteActionResult castVote(int option, String sender) {
        int totalVotesCastAlready = 0;

        for (PollWidgetController pollWidgetController : pollWidgetControllerList) {
            if (pollWidgetController.getVoters().contains(sender)) {
                if (pollWidgetController.getOptionId() == option) {
                    return VoteActionResult.ALREADY_VOTED_FOR_OPTION;
                }

                ++totalVotesCastAlready;
            }
        }

        if (totalVotesCastAlready >= this.multi) {
            if (this.multi == 1) {
                return VoteActionResult.VOTE_ALREADY_CAST;
            } else {
                return VoteActionResult.MULTIVOTE_ALREADY_CAST;
            }
        } else {
            return this.castVoteAnyway(option, sender);
        }
    }

    public VoteActionResult castVoteAnyway(int option, String sender) {
        for (PollWidgetController pollWidgetController : pollWidgetControllerList) {
            if (pollWidgetController.getOptionId() == option && StringUtils.isNotBlank(pollWidgetController.getOptionText())) {
                pollWidgetController.vote(sender);
                return VoteActionResult.SUCCESSFULLY_VOTED_OR_CHANGED;
            }
        }

        return VoteActionResult.INVALID_VOTE;
    }

    public int getHighestAmountOfVotes() {
        int votes = 0;
        List<PollWidgetController> pollWidgetWithHighestVotes = this.getPollWidgetWithHighestVotes();
        if (!pollWidgetWithHighestVotes.isEmpty()) {
            votes = pollWidgetWithHighestVotes.get(0).getVotes();
        }

        return votes;
    }

    private List<PollWidgetController> getPollWidgetWithHighestVotes() {
        List<PollWidgetController> highests = new ArrayList();
        int highest = 0;

        for (PollWidgetController pollWidgetController : pollWidgetControllerList) {
            if (!StringUtils.isBlank(pollWidgetController.getOptionText())) {
                if (pollWidgetController.getVotes() > highest) {
                    highest = pollWidgetController.getVotes();
                    highests.clear();
                    highests.add(pollWidgetController);
                } else if (pollWidgetController.getVotes() == highest) {
                    highests.add(pollWidgetController);
                }
            }
        }

        return highests;
    }

    public VoteActionResult removeVote(int option, String sender) {
        if ((long) option >= this.getActiveOptionsCount()) {
            return VoteActionResult.INVALID_VOTE;
        } else {
            List<PollWidgetController> controllersForSender = this.getControllersForSenderVote(sender);
            if (controllersForSender.isEmpty()) {
                return VoteActionResult.NOT_YET_VOTED;
            } else {
                Optional<PollWidgetController> actualController = controllersForSender.stream().filter((c) -> c.getOptionId() == option).findAny();
                if (actualController.isPresent()) {
                    actualController.get().removeVote(sender);
                    this.reloadView();
                    return VoteActionResult.REMOVED_VOTE;
                } else {
                    return VoteActionResult.NOT_VOTED_FOR;
                }
            }
        }
    }

    public long getActiveOptionsCount() {
        return this.getOptions().stream().filter((o) -> !o.getOptionText().equals("")).count();
    }

    public VoteActionResult changeVote(int oldOption, int newOption, String sender) {
        if (this.multi > 1 && newOption == oldOption) {
            return VoteActionResult.IMPROPER_CHANGE_MULTIVOTE;
        } else if ((long) newOption >= this.getActiveOptionsCount()) {
            return VoteActionResult.INVALID_VOTE_NEW;
        } else if ((long) oldOption >= this.getActiveOptionsCount()) {
            return VoteActionResult.INVALID_VOTE_OLD;
        } else {
            List<PollWidgetController> controllersForSender = this.getControllersForSenderVote(sender);
            if (controllersForSender.isEmpty()) {
                return VoteActionResult.NOT_YET_VOTED;
            } else if (newOption == oldOption && controllersForSender.size() == 1) {
                PollWidgetController controllerForSender = controllersForSender.get(0);
                if (controllerForSender.getOptionId() == newOption) {
                    return VoteActionResult.ALREADY_VOTED_FOR_OPTION;
                } else {
                    VoteActionResult voteActionResult = this.castVoteAnyway(newOption, sender);
                    if (voteActionResult == VoteActionResult.SUCCESSFULLY_VOTED_OR_CHANGED) {
                        controllerForSender.removeVote(sender);
                        this.reloadView();
                    }

                    return voteActionResult;
                }
            } else if (controllersForSender.stream().anyMatch((c) -> c.getOptionId() == oldOption)) {
                if (controllersForSender.stream().anyMatch((c) -> c.getOptionId() == newOption)) {
                    return VoteActionResult.ALREADY_VOTED_FOR_OPTION;
                } else {
                    this.removeVote(oldOption, sender);
                    this.castVote(newOption, sender);
                    return VoteActionResult.SUCCESSFULLY_VOTED_OR_CHANGED;
                }
            } else {
                return VoteActionResult.NOT_VOTED_FOR;
            }
        }
    }

    private List<PollWidgetController> getControllersForSenderVote(String sender) {
        List<PollWidgetController> votedControllers = new ArrayList();

        for (PollWidgetController pollWidgetController : pollWidgetControllerList) {
            if (pollWidgetController.getVoters().contains(sender)) {
                votedControllers.add(pollWidgetController);
            }
        }

        return votedControllers;
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
            if (pollWidgetController.getOptionId() == option) {
                return pollWidgetController.getOptionId() + 1 + ": {" + pollWidgetController.getOptionText() + "}";
            }
        }

        return null;
    }

    public int getOptionCount() {
        return this.options;
    }

    public String getQuestionText() {
        return this.taQuestion.getText().trim();
    }

    public List<PollOption> getOptions() {
        List<PollOption> optionList = new ArrayList();

        for (int i = 0; i < this.getOptionCount(); ++i) {
            PollWidgetController optionController = pollWidgetControllerList.get(i);
            optionList.add(new PollOption(optionController.getOptionText(), optionController.getVotes(), new ArrayList(optionController.getVoters())));
        }

        return optionList;
    }

    public void load(List<String> inputList) {
        this.btnClearAllClicked(null);
        this.btnClearAllClicked(null);
        this.tfOptions.setText(inputList.size() + "");
        this.tfOptionsAction(null);

        for (int i = 0; i < inputList.size(); ++i) {
            PollWidgetController optionController = pollWidgetControllerList.get(i);
            optionController.setOptionText(inputList.get(i));
        }

    }

    public void load(PollSave pollSave) {
        try {
            this.btnClearAllClicked(null);
            this.btnClearAllClicked(null);
            this.tfOptions.setText(pollSave.getOptionCount() + "");
            this.tfOptionsAction(null);

            for (int i = 0; i < pollSave.getOptions().size(); ++i) {
                PollOption pollOption = pollSave.getOptions().get(i);
                PollWidgetController optionController = pollWidgetControllerList.get(i);
                optionController.setOptionText(pollOption.getOptionText());
                optionController.setVoters(pollOption.getVoters());
                optionController.setVotes(pollOption.getVotes());
            }

            this.taQuestion.setText(pollSave.getQuestion());
        } catch (Exception e) {
            SimplePopup.showPopupError("File cannot be loaded, are you sure it is a Polls save?\n\nException: " + e.getMessage() + "\n" + e.getStackTrace()[0]);
            LOG.error("File cannot be loaded", e);
        }

    }

    private void addWithDragging(VBox root, Parent node) {
        node.setOnDragDetected((event) -> node.startFullDrag());
        node.setOnMouseDragOver((event) -> {
            String id = ((Node) event.getTarget()).getId();
            if (id.equals("paneHoverBottom")) {
                hoverSide = "paneHoverBottom";
            } else if (id.equals("paneHoverTop")) {
                hoverSide = "paneHoverTop";
            }

            if (!previousHoverSide.equals(hoverSide)) {
                previousHoverSide = hoverSide;
                if (hoverSide.equals("paneHoverTop")) {
                    node.setStyle("-fx-border-color: " + this.highlight + " transparent transparent transparent; -fx-border-width: 1 0 1 0;");
                } else if (hoverSide.equals("paneHoverBottom")) {
                    node.setStyle("-fx-border-color: transparent transparent" + this.highlight + " transparent; -fx-border-width: 1 0 1 0;");
                }
            }

        });
        node.setOnMouseDragEntered((event) -> node.getChildrenUnmodifiable().stream().filter((node1) -> node1.getId().equals("stackPane")).findFirst().flatMap((stackPane) -> ((Parent) stackPane).getChildrenUnmodifiable().stream().filter((node1) -> node1.getId().equals("gpHover")).findFirst()).ifPresent((hoverNode) -> {
            hoverNode.setDisable(false);
            hoverNode.setVisible(true);
        }));
        node.setOnMouseDragExited((event) -> {
            node.getChildrenUnmodifiable().stream().filter((node1) -> node1.getId().equals("stackPane")).findFirst().flatMap((stackPane) -> ((Parent) stackPane).getChildrenUnmodifiable().stream().filter((node1) -> node1.getId().equals("gpHover")).findFirst()).ifPresent((hoverNode) -> {
                hoverNode.setDisable(true);
                hoverNode.setVisible(false);
            });
            node.setStyle("-fx-border-color: transparent; -fx-border-width: 1 0 1 0;");
        });
        node.setOnMouseDragReleased((event) -> {
            node.setStyle("");
            int indexOfDraggingNode = root.getChildren().indexOf(event.getGestureSource());
            int indexOfDropTarget = root.getChildren().indexOf(node);
            if (hoverSide.equals("paneHoverTop")) {
                --indexOfDropTarget;
            }

            if (indexOfDropTarget < indexOfDraggingNode) {
                ++indexOfDropTarget;
            }

            this.rotateNodes(indexOfDraggingNode, indexOfDropTarget);
            event.consume();
        });
        root.getChildren().add(node);
    }

    private void rotateNodes(int indexOfDraggingNode, int indexOfDropTarget) {
        if (indexOfDraggingNode >= 0 && indexOfDropTarget >= 0) {
            PollWidgetController tempController = new PollWidgetController();
            PollWidgetController draggingController = pollWidgetControllerList.get(indexOfDraggingNode);
            tempController.copyData(draggingController);
            if (indexOfDraggingNode > indexOfDropTarget) {
                LOG.info("Rotating up, " + indexOfDraggingNode + "->" + indexOfDropTarget);

                for (int i = indexOfDraggingNode - 1; i >= indexOfDropTarget; --i) {
                    pollWidgetControllerList.get(i + 1).copyData(pollWidgetControllerList.get(i));
                }

                pollWidgetControllerList.get(indexOfDropTarget).copyData(tempController);
            } else if (indexOfDraggingNode < indexOfDropTarget) {
                LOG.info("Rotating down, " + indexOfDraggingNode + "->" + indexOfDropTarget);

                for (int i = indexOfDraggingNode; i < indexOfDropTarget; ++i) {
                    pollWidgetControllerList.get(i).copyData(pollWidgetControllerList.get(i + 1));
                }

                pollWidgetControllerList.get(indexOfDropTarget).copyData(tempController);
            }

            this.reloadView();
        }

    }

    public int getMultiVoteCount() {
        try {
            return Integer.parseInt(this.tfMulti.getText());
        } catch (NumberFormatException var2) {
            LOG.error("Cannot convert " + this.tfMulti.getText() + " into an integer. Defaulting to 1");
            return 1;
        }
    }

    public NominationResult nominate(String textToNominate, TwitchUser sender) {
        if (!cbNomination.isSelected()) return NominationResult.DISABLED;
        if (StringUtils.isBlank(textToNominate)) return NominationResult.NO_VALUE;

        String combinedPollText = textToNominate + " - " + sender.getDisplayName();

        for (PollWidgetController pollWidgetController : pollWidgetControllerList) {
            String optionText = pollWidgetController.getOptionText();
            if ((optionText.contains("-") && optionText.substring(0, optionText.lastIndexOf("-")-1).equalsIgnoreCase(textToNominate)) || optionText.equalsIgnoreCase(textToNominate)) {
                return NominationResult.ALREADY_PRESENT;
            }
        }
        for (PollWidgetController pollWidgetController : pollWidgetControllerList.stream().sorted(Comparator.comparing(PollWidgetController::getOptionId)).collect(Collectors.toList())) {
            String optionText = pollWidgetController.getOptionText();
            if (StringUtils.isBlank(optionText)) {
                pollWidgetController.setOptionText(combinedPollText);
                return NominationResult.SUCCESSFULLY_ADDED;
            }
        }
        return NominationResult.NOT_ENOUGH_OPTIONS;
    }
}
