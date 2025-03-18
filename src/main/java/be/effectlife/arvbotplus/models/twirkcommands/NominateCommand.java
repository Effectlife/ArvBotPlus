package be.effectlife.arvbotplus.models.twirkcommands;

import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.controllers.scenes.PollController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.MessageKey;
import be.effectlife.arvbotplus.loading.MessageProperties;
import be.effectlife.arvbotplus.loading.Scenes;
import be.effectlife.arvbotplus.utilities.BasicUtilities;
import com.gikk.twirk.Twirk;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NominateCommand extends BaseCommand {


    public static final String PATTERN = MessageProperties.getString(MessageKey.TWIRK_PATTERN_PREFIX) + MessageProperties.getString(MessageKey.TWIRK_PATTERN_COMMAND_NOMINATE);
    private final PollController pollController;

    public NominateCommand(Twirk twirk, boolean disable) {
        super(CommandType.CONTENT_COMMAND, twirk, disable);
        pollController = (PollController) AESceneLoader.getInstance().getController(Scenes.S_POLL);
        LOG = LoggerFactory.getLogger(NominateCommand.class);
    }

    @Override
    void handleCommand(String content, TwitchUser sender, TwitchMessage message) {
        Map<String, String> params = new HashMap<>();
        params.put("sender", sender.getDisplayName());

        if(content.trim().equals(PATTERN) || content.trim().equals(NominateShortCommand.PATTERN)) {
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_NOMINATION_HELP, params));
            return;
        }
        String textToNominate = content.substring(content.indexOf(' ')).trim();
        NominationResult nominate = pollController.nominate(textToNominate, sender);
        params.put("nomination", textToNominate);
        switch (nominate) {
            case DISABLED:
                channelMessage(MessageProperties.getString(MessageKey.TWIRK_MESSAGE_NOMINATION_DISABLED));
                break;
            case NO_VALUE:
                channelMessage(MessageProperties.getString(MessageKey.TWIRK_MESSAGE_NOMINATION_NOVALUE));
                break;
            case SUCCESSFULLY_ADDED:
                channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_NOMINATION_SUCCESS, params));
                break;
            case ALREADY_PRESENT:
                channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_NOMINATION_PRESENT, params));
                break;
            case NOT_ENOUGH_OPTIONS:
                channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_NOMINATION_NOTENOUGHOPTIONS, params));
                break;
            default:
                LOG.error("Unhandled Nomination Result: {}", nominate);
        }
    }

    @Override
    protected String getCommandWords() {
        return PATTERN;
    }
}
