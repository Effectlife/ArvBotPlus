package be.effectlife.arvbotplus.models.twirkcommands;

import be.effectlife.arvbotplus.loading.MessageKey;
import be.effectlife.arvbotplus.loading.MessageProperties;
import com.gikk.twirk.Twirk;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;

public class NominateShortCommand extends BaseCommand {

    public static final String PATTERN = MessageProperties.getString(MessageKey.TWIRK_PATTERN_PREFIX) + MessageProperties.getString(MessageKey.TWIRK_PATTERN_COMMAND_NOMINATE_SHORT);

    private final NominateCommand nominateCommand;

    public NominateShortCommand(Twirk twirk, boolean disable, NominateCommand nominateCommand) {
        super(CommandType.CONTENT_COMMAND, twirk, disable);
        this.nominateCommand = nominateCommand;
    }

    @Override
    void handleCommand(String content, TwitchUser sender, TwitchMessage message) {
        nominateCommand.handleCommand(content, sender, message);
    }

    @Override
    protected String getCommandWords() {
        return PATTERN;
    }
}
