package be.effectlife.arvbotplus.models.twirkcommands;

import be.effectlife.arvbotplus.controllers.scenes.PollController;
import be.effectlife.arvbotplus.controllers.widgets.QuickPollWidgetController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.MessageKey;
import be.effectlife.arvbotplus.loading.MessageProperties;
import be.effectlife.arvbotplus.loading.Scenes;
import com.gikk.twirk.Twirk;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ChangeVoteCommand extends BaseCommand {
    private static final AESceneLoader sceneloader;
    private static final String SENDER = "sender";
    private static final String VOTEVALUE = "votevalue";
    public static final String PATTERN = MessageProperties.getString(MessageKey.TWIRK_PATTERN_PREFIX) + MessageProperties.getString(MessageKey.TWIRK_PATTERN_COMMAND_CHANGEVOTE);

    static {
        sceneloader = AESceneLoader.getInstance();
    }

    public ChangeVoteCommand(Twirk twirk, boolean disable) {
        super(CommandType.CONTENT_COMMAND, twirk, disable);
        LOG = LoggerFactory.getLogger(ChangeVoteCommand.class);
    }

    protected void handleCommand(String content, TwitchUser sender, TwitchMessage message) {
        String messageContent = content.replaceAll("( +)", " ").trim().toLowerCase();
        String[] split = messageContent.split(" ");
        Map<String, String> params = new HashMap<>();
        params.put(SENDER, sender.getDisplayName());
        params.put("pattern", PATTERN);
        if (content.equals(PATTERN) ||
                (split.length > 1 && split[1].equals("options")) ||
                (split.length > 1 && split[1].equals("option")) ||
                (split.length > 1 && split[1].equals("help"))
        ) {
            //No additional params, help, option or options as param given. Print out instructions

            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CHANGEVOTE_HELP, params));
            return;
        }
        PollController pollController = (PollController) sceneloader.getController(Scenes.S_POLL);
        //try to parse the command

        switch (pollController.getPollType()) {
            case STANDARD:
                handleStandardPollCommand(split, sender.getDisplayName());
                break;
            case QUICK:
                handleQuickPollCommand(split, sender.getDisplayName());
                break;
            case NONE:
            case QP_CLEAR:
                handleNonePollCommand(split, sender.getDisplayName());
                break;
            default:
                LOG.error("An unknown PollType has been given... This should never happen...");
        }
    }

    private void handleStandardPollCommand(String[] split, String sender) {
        PollController pollController = (PollController)sceneloader.getController(Scenes.S_POLL);
        Map<String, String> params = new HashMap();
        params.put("sender", sender);

        try {
            int oldOption = Integer.parseInt(split[1]) - 1;
            int newOption = -1;
            if (split.length > 2) {
                newOption = Integer.parseInt(split[2]) - 1;
            }

            if (newOption == -1) {
                newOption = oldOption;
            }

            VoteActionResult addOptionResult = pollController.changeVote(oldOption, newOption, sender);
            params.put("votevalue", pollController.getOptionText(newOption));
            switch (addOptionResult) {
                case ALREADY_VOTED_FOR_OPTION:
                    this.channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CHANGEVOTE_ALREADYVOTED, params));
                    return;
                case SUCCESSFULLY_VOTED_OR_CHANGED:
                    this.channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CHANGEVOTE_ADDEDVOTE, params));
                    return;
                case IMPROPER_CHANGE_MULTIVOTE:
                    this.channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CHANGEVOTE_IMPROPER, params));
                    return;
                case NOT_YET_VOTED:
                    this.channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CHANGEVOTE_NOTYETVOTED, params));
                    return;
                case INVALID_VOTE:
                    this.channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CHANGEVOTE_INVALIDVOTE, params));
                    break;
                case INVALID_VOTE_NEW:
                    params.put("votevalue", String.valueOf(newOption + 1));
                    this.channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CHANGEVOTE_INVALIDVOTE, params));
                    break;
                case INVALID_VOTE_OLD:
                    params.put("votevalue", String.valueOf(oldOption + 1));
                    this.channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CHANGEVOTE_INVALIDVOTE, params));
                    break;
                case NOT_VOTED_FOR:
                    params.put("oldVote", pollController.getOptionText(oldOption));
                    this.channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CHANGEVOTE_NOTYETVOTED_MULTIVOTE, params));
                    break;
                default:
                    LOG.error("Unknown addOptionResult recieved: {}", addOptionResult);
            }
        } catch (NumberFormatException var8) {
            this.channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CHANGEVOTE_INVALIDVOTE, params));
        }

    }

    private void handleNonePollCommand(String[] split, String sender) {
        Map<String, String> params = new HashMap<>();
        params.put(VOTEVALUE, split[1]);
        params.put(SENDER, sender);
        channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CHANGEVOTE_NOPOLLACTIVE, params));
    }

    private void handleQuickPollCommand(String[] split, String sender) {
        QuickPollWidgetController quickPollWidgetController = (QuickPollWidgetController) sceneloader.getController(Scenes.W_QUICKPOLL);
        Map<String, String> params = new HashMap<>();
        params.put(VOTEVALUE, split[1]);
        params.put(SENDER, sender);
        try {
            int option = Integer.parseInt(split[1]);
            VoteActionResult addOptionResult = quickPollWidgetController.changeVote(option, sender);
            switch (addOptionResult) {
                case SUCCESSFULLY_VOTED_OR_CHANGED:
                    channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CHANGEVOTE_ADDEDVOTE, params));
                    break;
                case NOT_YET_VOTED:
                    channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CHANGEVOTE_NOTYETVOTED, params));
                    break;
                case ALREADY_VOTED_FOR_OPTION:
                    channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CHANGEVOTE_ALREADYVOTED, params));
                    break;
                default:
                    LOG.error("Unknown addOptionResult recieved: {}", addOptionResult);
                    break;
            }
        } catch (NumberFormatException nfe) {
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CHANGEVOTE_INVALIDVOTE, params));
        }
    }

    public String getCommandWords() {
        return PATTERN;
    }
}
