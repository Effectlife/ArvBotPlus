package be.effectlife.arvbotplus.twirk;

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

public class TwirkSystem {
    public void initializeSystem() throws IOException, InterruptedException {
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
        Twirk twirk = (new TwirkBuilder(channel, "HARDCODED", "oauth:" + properties.getProperty("twitch.bot.oauthtoken"))).setVerboseMode(true).build();
        twirk.addIrcListener(getOnDisconnectListener(twirk));
        twirk.addIrcListener(new ConvCommand(twirk));
        twirk.addIrcListener(new VoteCommand(twirk));
        twirk.addIrcListener(new ChangeVoteCommand(twirk));
        System.out.println("Conversionbot is loading");
        Thread.sleep(2000L);
        twirk.connect();

        twirk.channelMessage("Conversionbot has successfully loaded. Use !conv to print help");
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
}
