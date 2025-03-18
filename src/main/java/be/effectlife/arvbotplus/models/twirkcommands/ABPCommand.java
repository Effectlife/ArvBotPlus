package be.effectlife.arvbotplus.models.twirkcommands;

import be.effectlife.arvbotplus.loading.MessageKey;
import be.effectlife.arvbotplus.loading.MessageProperties;
import com.gikk.twirk.Twirk;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ABPCommand extends BaseCommand {


    public static final String PATTERN = MessageProperties.getString(MessageKey.TWIRK_PATTERN_PREFIX) + MessageProperties.getString(MessageKey.TWIRK_PATTERN_COMMAND_ABP);


    public ABPCommand(Twirk twirk, boolean disable) {
        super(CommandType.CONTENT_COMMAND, twirk, disable);
        LOG = LoggerFactory.getLogger(ABPCommand.class);
    }

    protected String getCommandWords() {
        return PATTERN;
    }

    @Override
    protected void performCommand(String command, TwitchUser sender, TwitchMessage message) {
        Map<String, String> params = new HashMap<>();
        params.put("commands", getCommands());
        channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_ABP_HELP, params));
    }

    @Override
    void handleCommand(String content, TwitchUser sender, TwitchMessage message) {
        //Unused
    }

    private String getCommands() {
        List<String> allSiblings = MessageProperties.getAllSiblings(MessageKey.TWIRK_PATTERN_COMMAND_ABP);
        StringBuilder stringBuilder = new StringBuilder();
        allSiblings.forEach(s -> stringBuilder.append(MessageProperties.getString(MessageKey.TWIRK_PATTERN_PREFIX)).append(MessageProperties.getString(s)).append("; "));
        return stringBuilder.toString();
    }
}
