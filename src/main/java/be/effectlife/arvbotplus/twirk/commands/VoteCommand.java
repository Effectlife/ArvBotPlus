package be.effectlife.arvbotplus.twirk.commands;

import be.effectlife.arvbotplus.controllers.scenes.PollController;
import be.effectlife.arvbotplus.controllers.widgets.QuickPollWidgetController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.MessageKey;
import be.effectlife.arvbotplus.loading.MessageProperties;
import be.effectlife.arvbotplus.loading.Scenes;
import com.gikk.twirk.Twirk;
import com.gikk.twirk.commands.CommandExampleBase;
import com.gikk.twirk.enums.USER_TYPE;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class VoteCommand extends CommandExampleBase {
    public static final String PATTERN = MessageProperties.getString(MessageKey.TWIRK_PATTERN_PREFIX) + MessageProperties.getString(MessageKey.TWIRK_PATTERN_COMMAND_VOTE);
    private static final Logger LOG = LoggerFactory.getLogger(VoteCommand.class);
    private static final AESceneLoader sceneloader;
    private static final String SENDER = "sender";
    private static final String VOTEVALUE = "votevalue";
    static {
        sceneloader = AESceneLoader.getInstance();
    }

    private final Twirk twirk;
    private final boolean disable;
    private final Map<String, String> params;

    public VoteCommand(Twirk twirk, boolean disable) {
        super(CommandType.CONTENT_COMMAND);
        this.twirk = twirk;
        this.disable = disable;
        params = new HashMap<>();
        params.put("pattern", PATTERN);
    }

    protected String getCommandWords() {
        return PATTERN;
    }

    protected USER_TYPE getMinUserPrevilidge() {
        return USER_TYPE.DEFAULT;
    }

    protected void performCommand(String command, TwitchUser sender, TwitchMessage message) {
        String content = message.getContent().trim();
        if (!content.startsWith(PATTERN)) return;
        String messagecontent = content.replaceAll("( +)", " ").trim().toLowerCase();
        String[] split = messagecontent.split(" ");
        params.put(SENDER, sender.getDisplayName());

        if (content.equals(PATTERN) ||
                (split.length > 1 && split[1].equals("options")) ||
                (split.length > 1 && split[1].equals("option")) ||
                (split.length > 1 && split[1].equals("help"))
        ) {
            //No additional params, help, option or options as param given. Print out instructions
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_VOTE_HELP, params));
            return;
        }
        PollController pollController = (PollController) sceneloader.getController(Scenes.S_POLL);
        //try to parse the command
        params.put(SENDER, sender.getDisplayName());
        switch (pollController.getPollType()) {
            case NONE:
                handleNonePollCommand();
                break;
            case STANDARD:
                handleStandardPollCommand(split, sender.getDisplayName());
                break;
            case QUICK:
                handleQuickPollCommand(split, sender.getDisplayName());
                break;
            default:
                LOG.error("An unknown PollType has been given... This should never happen...");
        }
    }

    private void handleNonePollCommand() {
        channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_VOTE_NOPOLLACTIVE, params));
    }

    private void handleStandardPollCommand(String[] split, String sender) {
        try {
            int option = Integer.parseInt(split[1]) - 1;//-1 to offset lists starting with 0
            PollController pollController = (PollController) sceneloader.getController(Scenes.S_POLL);
            VoteActionResult voteResult = pollController.castVote(option, sender);
            params.put(VOTEVALUE, pollController.getOptionText(option));
            handleVoteResult(voteResult);
        } catch (NumberFormatException nfe) {
            params.put(VOTEVALUE, split[1]);
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_VOTE_INVALIDVOTE, params));

        }
    }


    private void handleQuickPollCommand(String[] split, String sender) {
        QuickPollWidgetController quickPollWidgetController = (QuickPollWidgetController) sceneloader.getController(Scenes.W_QUICKPOLL);
        try {
            int option = Integer.parseInt(split[1]);
            VoteActionResult voteResult = quickPollWidgetController.castVote(option, sender);
            params.put(VOTEVALUE, split[1]);
            handleVoteResult(voteResult);
        } catch (NumberFormatException nfe) {
            params.put(VOTEVALUE, split[1]);
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_VOTE_INVALIDVOTE, params));
        }
    }


    private void handleVoteResult(VoteActionResult voteResult) {

        if (voteResult == VoteActionResult.SUCCESSFULLY_VOTED_OR_CHANGED) {
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_VOTE_ADDEDVOTE, params));
        } else if (voteResult == VoteActionResult.VOTE_ALREADY_CAST) {
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_VOTE_ALREADYVOTED, params));
        } else if (voteResult == VoteActionResult.INVALID_VOTE) {
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_VOTE_INVALIDVOTE, params));
        } else LOG.error("Unknown voteResult recieved: {}", voteResult);
        reloadView();
    }

    public void reloadView() {
        (sceneloader.getController(Scenes.S_POLL)).reloadView();
    }

    private void channelMessage(String message) {
        if (this.disable) {
            LOG.trace(message);
        } else {
            twirk.channelMessage(message);
        }
    }
}
