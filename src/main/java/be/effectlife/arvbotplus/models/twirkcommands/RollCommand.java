package be.effectlife.arvbotplus.models.twirkcommands;

import be.effectlife.arvbotplus.controllers.scenes.DiceController;
import be.effectlife.arvbotplus.controllers.scenes.QuestionsController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.MessageKey;
import be.effectlife.arvbotplus.loading.MessageProperties;
import be.effectlife.arvbotplus.loading.Scenes;
import be.effectlife.arvbotplus.utilities.Origin;
import com.gikk.twirk.Twirk;
import com.gikk.twirk.commands.CommandExampleBase;
import com.gikk.twirk.enums.USER_TYPE;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class RollCommand extends CommandExampleBase {
    public static final String PATTERN = MessageProperties.getString(MessageKey.TWIRK_PATTERN_PREFIX) + MessageProperties.getString(MessageKey.TWIRK_PATTERN_COMMAND_ROLL);
    private static final Logger LOG = LoggerFactory.getLogger(RollCommand.class);
    private final Twirk twirk;
    private final boolean disable;

    public RollCommand(Twirk twirk, boolean disable) {
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
        String contentFiltered = content.replace(PATTERN, "").trim();
        HashMap<String, String> options = new HashMap<>();
        options.put("pattern", PATTERN);
        options.put("sender", sender.getDisplayName());
        options.put("question", contentFiltered);
        if (message.getContent().trim().equals(PATTERN)) {
            //return instructions
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_ROLL_HELP, options));
            return;
        }

        //Execute the roll
        ((DiceController)AESceneLoader.getInstance().getController(Scenes.S_DICE)).doRoll(Origin.CHAT);

        channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_DO_ROLL, options));
    }




    private void channelMessage(String message) {
        if (this.disable) {
            LOG.trace(message);
        } else {
            twirk.channelMessage(message);
        }
    }
}
