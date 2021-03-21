package be.effectlife.arvbotplus.twirk;

import be.effectlife.arvbotplus.Main;
import be.effectlife.arvbotplus.twirk.commands.ABPCommand;
import be.effectlife.arvbotplus.twirk.commands.ChangeVoteCommand;
import be.effectlife.arvbotplus.twirk.commands.VoteCommand;
import be.effectlife.arvbotplus.utilities.SimplePopup;
import com.gikk.twirk.Twirk;
import com.gikk.twirk.TwirkBuilder;
import com.gikk.twirk.events.TwirkListener;

import java.io.*;
import java.net.SocketException;
import java.util.Properties;

import be.effectlife.arvbotplus.twirk.commands.ConvCommand;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwirkSystem {
    private static final Logger LOG = LoggerFactory.getLogger(TwirkSystem.class);
    private Twirk twirk;
    private boolean disable;

    public void initializeSystem(Properties properties, boolean disable) throws IOException, InterruptedException {
        this.disable = disable;
        if (this.disable) {
            LOG.debug("Disabled twirk system");
            return;
        }
        String channel = properties.getProperty("twitch.channel");
        twirk = (new TwirkBuilder(channel, "ArvBotPlus", "oauth:" + properties.getProperty("twitch.bot.oauthtoken"))).setVerboseMode(false).build();
        twirk.addIrcListener(getOnDisconnectListener(twirk));
        twirk.addIrcListener(new ConvCommand(twirk, disable));
        twirk.addIrcListener(new VoteCommand(twirk, disable));
        twirk.addIrcListener(new ChangeVoteCommand(twirk, disable));
        twirk.addIrcListener(new ABPCommand(twirk, disable));
        LOG.info("ArvBotPlus is loading");
        Thread.sleep(500L);

        try{
            boolean connection = twirk.connect();
            if (!connection) {
                Platform.runLater(() -> {
                    SimplePopup.showPopupWarn("Connection to twitch failed. Please check your configuration and try again.");
                    System.exit(1);
                });
            }
            channelMessage("ArvBotPlus has loaded. Use " + Main.PREFIX + "abp to see available commands");
        }catch (SocketException e){
            Platform.runLater(()->SimplePopup.showPopupError("Could not connect to twitch, please try again. If this happens more than 3 times in sequence, please report this as an issue. "));
            LOG.error("Socket exception: ", e);
        }
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

    public void disconnect() {
        if(!this.disable || (this.twirk != null && this.twirk.isConnected())){
            twirk.disconnect();
        }
    }
}
