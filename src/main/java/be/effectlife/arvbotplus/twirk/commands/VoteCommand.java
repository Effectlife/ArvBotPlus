package be.effectlife.arvbotplus.twirk.commands;

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

public class VoteCommand extends CommandExampleBase {
    private static final Logger LOG = LoggerFactory.getLogger(VoteCommand.class);
    public static final String PATTERN = "&vote";
    private final Twirk twirk;
    private static final AESceneLoader sceneloader;

    static {
        sceneloader = AESceneLoader.getInstance();
    }

    public VoteCommand(Twirk twirk) {
        super(CommandType.CONTENT_COMMAND);
        this.twirk = twirk;
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
            channelMessage("Hi " + sender.getDisplayName() + ", &vote uses the following syntax: " +
                    "\"&vote {option}\"; To change your vote, use \"&changevote {option}\"");
            return;
        }
        PollController pollController = (PollController) sceneloader.getController(Scenes.S_POLL);
        //try to parse the command

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
        channelMessage("There is currently no poll active.");
    }

    private void handleStandardPollCommand(String[] split, String sender) {
        try {
            int option = Integer.parseInt(split[1]) - 1;//-1 to offset lists starting with 0
            PollController pollController = (PollController) sceneloader.getController(Scenes.S_POLL);
            int voteResult = pollController.castVote(option, sender);
            handleVoteResult(split, sender, voteResult);
        } catch (NumberFormatException nfe) {
            channelMessage("Sorry " + sender + ", {" + split[1] + "} is not a valid option. Please try again.");
        }
    }

    private void handleQuickPollCommand(String[] split, String sender) {
        QuickPollWidgetController quickPollWidgetController = (QuickPollWidgetController) sceneloader.getController(Scenes.W_QUICKPOLL);
        try {
            int option = Integer.parseInt(split[1]);
            int voteResult = quickPollWidgetController.castVote(option, sender);
            handleVoteResult(split, sender, voteResult);
        } catch (NumberFormatException nfe) {
            channelMessage("Sorry " + sender + ", {" + split[1] + "} is not a valid option. Please try again.");
        }
    }


    private void handleVoteResult(String[] split, String sender, int voteResult) {
        if (voteResult == 0) {
            //Succesfull added. //TODO: Add username to delayed channelmessage
            channelMessage("Thanks for voting for {" + split[1] + "}, " + sender);
        } else if (voteResult == 1) {
            channelMessage("Sorry " + sender + ", you have already voted. If you wish to change your vote, use &changevote {option}");
        } else if (voteResult == 2) {
            channelMessage("Sorry " + sender + ", '" + split[1] + "' is not a valid option. Please try again.");
        } else LOG.error("Unknown voteResult recieved: " + voteResult);
        reloadView();
    }
    public void reloadView() {
        (sceneloader.getController(Scenes.S_POLL)).reloadView();
    }
    private void channelMessage(String message) {
        twirk.channelMessage(message);
    }
}
