package be.effectlife.arvbotplus.models.twirkcommands;

import be.effectlife.arvbotplus.loading.MessageKey;
import be.effectlife.arvbotplus.loading.MessageProperties;
import com.gikk.twirk.Twirk;
import com.gikk.twirk.commands.CommandExampleBase;
import com.gikk.twirk.enums.USER_TYPE;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ABPCommand extends CommandExampleBase {
    public static final String PATTERN = MessageProperties.getString(MessageKey.TWIRK_PATTERN_PREFIX) + MessageProperties.getString(MessageKey.TWIRK_PATTERN_COMMAND_ABP);
    private static final Logger LOG = LoggerFactory.getLogger(ABPCommand.class);
    private final Twirk twirk;
    private final boolean disable;

    public ABPCommand(Twirk twirk, boolean disable) {
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
        Map<String, String> params = new HashMap<>();
        params.put("commands", getCommands());
        channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_ABP_HELP, params));
    }

    private String getCommands() {
        List<String> allSiblings = MessageProperties.getAllSiblings(MessageKey.TWIRK_PATTERN_COMMAND_ABP);
        StringBuilder stringBuilder = new StringBuilder();
        allSiblings.forEach(s -> stringBuilder.append(MessageProperties.getString(MessageKey.TWIRK_PATTERN_PREFIX)).append(MessageProperties.getString(s)).append("; "));
        return stringBuilder.toString();
    }

    private void channelMessage(String message) {
        if (this.disable) {
            LOG.trace(message);
        } else {
            twirk.channelMessage(message);
        }
    }
}
