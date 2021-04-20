package be.effectlife.arvbotplus.twirk;

import be.effectlife.arvbotplus.controllers.scenes.ConversionController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.MessageKey;
import be.effectlife.arvbotplus.loading.MessageProperties;
import be.effectlife.arvbotplus.loading.Scenes;
import be.effectlife.arvbotplus.twirk.commands.ABPCommand;
import be.effectlife.arvbotplus.twirk.commands.ChangeVoteCommand;
import be.effectlife.arvbotplus.twirk.commands.VoteCommand;
import be.effectlife.arvbotplus.utilities.SimplePopup;
import com.gikk.twirk.Twirk;
import com.gikk.twirk.TwirkBuilder;
import com.gikk.twirk.events.TwirkListener;

import java.io.*;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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

        String channelFormatted = channel.substring(0, 1).toUpperCase() + channel.substring(1);
        twirk = (new TwirkBuilder(channel, channelFormatted, "oauth:" + properties.getProperty("twitch.bot.oauthtoken"))).setVerboseMode(false).build();
        twirk.addIrcListener(getOnDisconnectListener(twirk));
        twirk.addIrcListener(new ConvCommand(twirk, disable));
        twirk.addIrcListener(new VoteCommand(twirk, disable));
        twirk.addIrcListener(new ChangeVoteCommand(twirk, disable));
        twirk.addIrcListener(new ABPCommand(twirk, disable));
        LOG.info("ArvBotPlus is loading");
        Thread.sleep(500L);

        try {
            boolean connection = twirk.connect();
            if (!connection) {
                Platform.runLater(() -> {
                    SimplePopup.showPopupWarn("Connection to twitch failed. Please check your configuration and try again.");
                    System.exit(1);
                });
            }
            Map<String, String> params = new HashMap<>();
            params.put("patternabp", MessageProperties.getString(MessageKey.TWIRK_PATTERN_PREFIX) + MessageProperties.getString(MessageKey.TWIRK_PATTERN_COMMAND_ABP));
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_STARTUP, params));
        } catch (SocketException e) {
            Platform.runLater(() -> SimplePopup.showPopupError("Could not connect to twitch, please try again. If this happens more than 3 times in sequence, please report this as an issue. "));
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
        if (twirk != null && (!this.disable || this.twirk.isConnected())) {
            twirk.disconnect();
            twirk = null;
        }
        ((ConversionController) AESceneLoader.getInstance().getController(Scenes.S_CONV)).checkTwirk();
    }

    public String getConnectedChannel() {
        if (!this.disable || (this.twirk != null && this.twirk.isConnected())) {
            return this.twirk.getNick();
        }
        return "Not Connected";
    }

    public boolean isLoaded() {
        return twirk != null && twirk.isConnected();
    }
}
