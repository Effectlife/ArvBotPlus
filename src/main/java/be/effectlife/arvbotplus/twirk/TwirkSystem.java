package be.effectlife.arvbotplus.twirk;

import be.effectlife.arvbotplus.Main;
import be.effectlife.arvbotplus.twirk.commands.ChangeVoteCommand;
import be.effectlife.arvbotplus.twirk.commands.VoteCommand;
import be.effectlife.arvbotplus.utilities.SimplePopup;
import com.gikk.twirk.Twirk;
import com.gikk.twirk.TwirkBuilder;
import com.gikk.twirk.events.TwirkListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import be.effectlife.arvbotplus.twirk.commands.ConvCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwirkSystem {
    private static final Logger LOG = LoggerFactory.getLogger(TwirkSystem.class);
    private Twirk twirk;
    private boolean disable;

    public void initializeSystem(boolean disable) throws IOException, InterruptedException {
        this.disable = disable;
        if (this.disable) {
            LOG.debug("Disabled twirk system");
            return;
        }
        Properties properties = new Properties();
        String propFileName = "./config.properties";
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(propFileName);
        } catch (FileNotFoundException e) {
            SimplePopup.showPopupError("Cannot find Twirk properties file " + propFileName + "; exiting program.");
            System.exit(1);
        }
        properties.load(inputStream);
        String channel = properties.getProperty("twitch.channel");
        twirk = (new TwirkBuilder(channel, "HARDCODED", "oauth:" + properties.getProperty("twitch.bot.oauthtoken"))).setVerboseMode(true).build();
        twirk.addIrcListener(getOnDisconnectListener(twirk));
        twirk.addIrcListener(new ConvCommand(twirk));
        twirk.addIrcListener(new VoteCommand(twirk));
        twirk.addIrcListener(new ChangeVoteCommand(twirk));
        System.out.println("Conversionbot is loading");
        Thread.sleep(2000L);
        int retries = 5;
        boolean connection = twirk.connect();
        while (!connection && retries > 0) {
            LOG.warn("Twirk failed to connect");
            retries--;
            Thread.sleep(1000);
            connection = twirk.connect();
        }
        twirk.channelMessage("ArvBotPlus has loaded. Use "+ Main.PREFIX+"abp ");
    }

    private static TwirkListener getOnDisconnectListener(final Twirk twirk) {
        return new TwirkListener() {
            public void onDisconnect() {
                try {
                    if (!twirk.connect()) {
                        twirk.close();
                    }
                } catch (IOException var2) {
                    twirk.close();
                } catch (InterruptedException ignored) {
                }

            }
        };
    }

    public void channelMessage(String message) {
        if (this.disable) {
            LOG.trace(message);
        } else {
            twirk.channelMessage(message);
        }
    }

}
