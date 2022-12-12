package be.effectlife.arvbotplus.models.twirkcommands;

import be.effectlife.arvbotplus.controllers.scenes.GiveawayController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.Scenes;
import be.effectlife.arvbotplus.utilities.GiveawayStatus;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

public class GiveawayListener extends BaseCommand {
    private final GiveawayController giveawayController;
    private final Properties properties;
    public static final String PATTERN = ""; //Listen to everything.


    public GiveawayListener(Properties properties) {
        super(CommandType.CONTENT_COMMAND);
        giveawayController = (GiveawayController) AESceneLoader.getInstance().getController(Scenes.S_GIVEAWAY);
        this.properties = properties;
    }

    protected String getCommandWords() {
        return PATTERN;
    }

    protected void handleCommand(String content, TwitchUser sender, TwitchMessage message) {
        if (giveawayController.getGiveawayStatus() == GiveawayStatus.RUNNING && StringUtils.equalsIgnoreCase(content, giveawayController.getKeyword())) {
            giveawayController.registerEntry(sender.getDisplayName());
        }

        if (StringUtils.equalsIgnoreCase(sender.getDisplayName(), properties.getProperty("twitch.giveaway.displayname")) && StringUtils.startsWith(content, "LAST CALL")) {
            //Last call message
            giveawayController.setLastCall();
        } else if (StringUtils.equalsIgnoreCase(sender.getDisplayName(), properties.getProperty("twitch.giveaway.displayname")) && StringUtils.startsWith(content, "[G")) {
            //Start parsing the message itself for information
            if (StringUtils.containsIgnoreCase(content, "Any pre-existing giveaway information cleared"))
                giveawayController.doInit();
            else if (StringUtils.containsIgnoreCase(content, "The giveaway has started! Please type"))
                giveawayController.setClearSetKeywordAndStart(content);
            else if (StringUtils.containsIgnoreCase(content, "you've just won the giveaway"))
                giveawayController.doWin(content);
        }

    }
}
