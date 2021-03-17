package be.effectlife.arvbotplus.twirk.commands;

import be.effectlife.arvbotplus.Main;
import be.effectlife.arvbotplus.controllers.scenes.PollController;
import be.effectlife.arvbotplus.controllers.widgets.QuickPollWidgetController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.Scenes;
import com.gikk.twirk.Twirk;
import com.gikk.twirk.commands.CommandExampleBase;
import com.gikk.twirk.enums.USER_TYPE;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeVoteCommand extends CommandExampleBase {
    private static final Logger LOG = LoggerFactory.getLogger(ChangeVoteCommand.class);
    private static final String PATTERN = Main.PREFIX + "changevote";
    private final Twirk twirk;
    private static final AESceneLoader sceneloader;
    private final boolean disable;

    static {
        sceneloader = AESceneLoader.getInstance();
    }

    public ChangeVoteCommand(Twirk twirk, boolean disable) {
        super(CommandType.CONTENT_COMMAND);
        this.twirk = twirk;
        this.disable = disable;
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

        if (content.equals(PATTERN) ||
                (split.length > 1 && split[1].equals("options")) ||
                (split.length > 1 && split[1].equals("option")) ||
                (split.length > 1 && split[1].equals("help"))
        ) {
            //No additional params, help, option or options as param given. Print out instructions
            channelMessage("Hi " + sender.getDisplayName() + ", " + Main.PREFIX + "changevote uses the following syntax: " +
                    "\"" + Main.PREFIX + "changevote {option}\"; If you haven't yet voted, use \"" + Main.PREFIX + "vote {option}\" to cast your vote");
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
                handleNonePollCommand();
                break;
            default:
                LOG.error("An unknown PollType has been given... This should never happen...");
        }
    }

    private void handleStandardPollCommand(String[] split, String sender) {
        PollController pollController = (PollController) sceneloader.getController(Scenes.S_POLL);
        try {
            int option = Integer.parseInt(split[1]) - 1;
            VoteActionResult addOptionResult = pollController.changeVote(option, sender);
            switch (addOptionResult) {
                case SAME_VOTE:
                    channelMessage("You already voted for " + split[1] + ", " + sender);
                    return;
                case ADDED:
                    channelMessage("Thanks for changing your vote to {" + split[1] + "}, " + sender);
                    return;
                case NO_VOTE_YET:
                    channelMessage("You haven't yet voted, " + sender + ", please vote with " + Main.PREFIX + "vote");
                    return;
                case INVALID_VOTE:
                    channelMessage("Sorry " + sender + ", '" + split[1] + "' is not a valid option. Please try again.");
            }
        } catch (NumberFormatException nfe) {
            channelMessage("Sorry " + sender + ", '" + split[1] + "' is not a valid option. Please try again.");
        }
    }

    private void handleNonePollCommand() {
        channelMessage("There is currently no poll active.");
    }

    private void handleQuickPollCommand(String[] split, String sender) {
        QuickPollWidgetController quickPollWidgetController = (QuickPollWidgetController) sceneloader.getController(Scenes.W_QUICKPOLL);
        try {
            int option = Integer.parseInt(split[1]);
            VoteActionResult addOptionResult = quickPollWidgetController.changeVote(option, sender);
            switch (addOptionResult) {
                case ADDED:
                    //Succesfull added. //TODO: Add username to delayed channelmessage
                    channelMessage("Thanks for changing your vote to {" + split[1] + "}, " + sender);
                    break;
                case ALREADY_VOTED:
                    channelMessage("You already voted for " + split[1] + ", " + sender);
                    break;
                case SAME_VOTE:
                    channelMessage("You haven't yet voted, " + sender + ", please vote with " + Main.PREFIX + "vote");
                    break;
                default:
                    LOG.error("Unknown addOptionResult recieved: " + addOptionResult);
                    break;
            }
        } catch (NumberFormatException nfe) {
            channelMessage("Sorry " + sender + ", '" + split[1] + "' is not a valid option. Please try again.");
        }
    }

    private void channelMessage(String message) {
        if (this.disable) {
            LOG.trace(message);
        } else {
            twirk.channelMessage(message);
        }
    }
}
