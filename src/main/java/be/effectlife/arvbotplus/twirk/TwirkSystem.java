package be.effectlife.arvbotplus.twirk;

import be.effectlife.arvbotplus.Main;
import be.effectlife.arvbotplus.twirk.commands.ChangeVoteCommand;
import be.effectlife.arvbotplus.twirk.commands.VoteCommand;
import be.effectlife.arvbotplus.utilities.SimplePopup;
import com.gikk.twirk.Twirk;
import com.gikk.twirk.TwirkBuilder;
import com.gikk.twirk.events.TwirkListener;

import java.io.*;
import java.util.Properties;

import be.effectlife.arvbotplus.twirk.commands.ConvCommand;
import javafx.application.Platform;
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
            Platform.runLater(() -> {
                SimplePopup.showPopupError("Cannot find Twirk properties file " + propFileName + "; Generating empty file and exiting program.");
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(propFileName));
                    bw.write("twitch.channel=<insert channel name (eg. gogcom)>\n" +
                            "twitch.bot.oauthtoken=<insert oauth token (eg. abc123abc123abc123abc123abc123)>");
                    bw.flush();
                    bw.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                System.exit(1);
            });
        }
        properties.load(inputStream);
        String channel = properties.getProperty("twitch.channel");
        twirk = (new TwirkBuilder(channel, "ArvBotPlus", "oauth:" + properties.getProperty("twitch.bot.oauthtoken"))).setVerboseMode(true).build();
        twirk.addIrcListener(getOnDisconnectListener(twirk));
        twirk.addIrcListener(new ConvCommand(twirk));
        twirk.addIrcListener(new VoteCommand(twirk));
        twirk.addIrcListener(new ChangeVoteCommand(twirk));
        LOG.info("ArvBotPlus is loading");
        Thread.sleep(500L);
        boolean connection = twirk.connect();
        if (!connection) {
            Platform.runLater(() -> {
                SimplePopup.showPopupWarn("Connection to twitch failed. Please check your configuration and try again.");
                System.exit(1);
            });
        }
        twirk.channelMessage("ArvBotPlus has loaded. Use " + Main.PREFIX + "abp ");
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
