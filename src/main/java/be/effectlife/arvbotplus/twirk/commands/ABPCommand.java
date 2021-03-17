package be.effectlife.arvbotplus.twirk.commands;

import be.effectlife.arvbotplus.Main;
import be.effectlife.arvbotplus.twirk.conversions.data.*;
import com.gikk.twirk.Twirk;
import com.gikk.twirk.commands.CommandExampleBase;
import com.gikk.twirk.enums.USER_TYPE;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class ABPCommand extends CommandExampleBase {
    private static final Logger LOG = LoggerFactory.getLogger(ABPCommand.class);
    private static final String PATTERN = Main.PREFIX + "abp";
    private final Twirk twirk;

    public ABPCommand(Twirk twirk) {
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
        LOG.info("Recieved: " + sender.getDisplayName() + ": " + message.getContent() + "");
        twirk.channelMessage("Available commands are: $conv, $vote, $changevote, $abp; Use each command without additional parameters to see more information about the command.");
    }
}
