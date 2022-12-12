package be.effectlife.arvbotplus.models.twirkcommands;

import be.effectlife.arvbotplus.controllers.scenes.DiceController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.MessageKey;
import be.effectlife.arvbotplus.loading.MessageProperties;
import be.effectlife.arvbotplus.loading.Scenes;
import com.gikk.twirk.Twirk;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class RollCommand extends BaseCommand {
    private static final Logger LOG = LoggerFactory.getLogger(RollCommand.class);
    public static final String PATTERN = MessageProperties.getString(MessageKey.TWIRK_PATTERN_PREFIX) + MessageProperties.getString(MessageKey.TWIRK_PATTERN_COMMAND_ROLL);


    public RollCommand(Twirk twirk, boolean disable) {
        super(CommandType.CONTENT_COMMAND, twirk, disable);
    }

    protected String getCommandWords() {
        return PATTERN;
    }

    protected void handleCommand(String content, TwitchUser sender, TwitchMessage message) {
        String contentFiltered = content.replace(PATTERN, "").trim();
        HashMap<String, String> options = new HashMap<>();
        options.put("pattern", PATTERN);
        options.put("sender", sender.getDisplayName());
        if (message.getContent().trim().equals(PATTERN)) {
            //return instructions
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_ROLL_HELP, options));
            return;
        }

        //Execute the roll
        Platform.runLater(() -> ((DiceController) AESceneLoader.getInstance().getController(Scenes.S_DICE)).doRollChat(contentFiltered, sender.getDisplayName()));

    }


    private void channelMessage(String message) {
        if (this.disable) {
            LOG.trace(message);
        } else {
            twirk.channelMessage(message);
        }
    }
}
