package be.effectlife.arvbotplus.models.twirkcommands;

import be.effectlife.arvbotplus.controllers.scenes.PollController;
import be.effectlife.arvbotplus.controllers.widgets.QuickPollWidgetController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.MessageKey;
import be.effectlife.arvbotplus.loading.MessageProperties;
import be.effectlife.arvbotplus.loading.Scenes;
import com.gikk.twirk.Twirk;
import com.gikk.twirk.commands.CommandExampleBase.CommandType;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoteCommand extends BaseCommand {
    private static final Logger LOG = LoggerFactory.getLogger(VoteCommand.class);
    private static final AESceneLoader sceneloader;
    private static final String SENDER = "sender";
    private static final String VOTEVALUE = "votevalue";

    public static final String PATTERN = MessageProperties.getString(MessageKey.TWIRK_PATTERN_PREFIX) + MessageProperties.getString(MessageKey.TWIRK_PATTERN_COMMAND_VOTE);

    static {
        sceneloader = AESceneLoader.getInstance();
    }

    private final Map<String, String> params;

    public VoteCommand(Twirk twirk, boolean disable) {
        super(CommandType.CONTENT_COMMAND, twirk, disable);
        params = new HashMap<>();
        params.put("pattern", PATTERN);
    }

    protected String getCommandWords() {
        return PATTERN;
    }

    protected void handleCommand(String content, TwitchUser sender, TwitchMessage message) {
        String messageContent = content.replaceAll("( +)", " ").trim().toLowerCase();
        String[] split = messageContent.split(" ");
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
        } else if (voteResult == VoteActionResult.ALREADY_VOTED_FOR_OPTION) {
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_MULTIVOTE_ALREADYVOTED, params));
        } else if (voteResult == VoteActionResult.MULTIVOTE_ALREADY_CAST) {
            params.put("maxvotes", String.valueOf(((PollController) sceneloader.getController(Scenes.S_POLL)).getMultiVoteCount()));
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_MULTIVOTE_ALREADY_CAST, params));
        } else {
            LOG.error("Unknown voteResult recieved: {}", voteResult);
        }

        reloadView();
    }

    public void reloadView() {
        (sceneloader.getController(Scenes.S_POLL)).reloadView();
    }
}
