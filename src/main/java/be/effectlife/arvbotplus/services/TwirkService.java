package be.effectlife.arvbotplus.services;

import be.effectlife.arvbotplus.controllers.scenes.ConversionController;
import be.effectlife.arvbotplus.controllers.scenes.InventoryController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.MessageKey;
import be.effectlife.arvbotplus.loading.MessageProperties;
import be.effectlife.arvbotplus.loading.Scenes;
import be.effectlife.arvbotplus.models.twirkcommands.*;
import be.effectlife.arvbotplus.utilities.SimplePopup;
import com.gikk.twirk.Twirk;
import com.gikk.twirk.TwirkBuilder;
import com.gikk.twirk.events.TwirkListener;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TwirkService {
    private static final Logger LOG = LoggerFactory.getLogger(TwirkService.class);
    private Twirk twirk;
    private boolean disable;

    private static TwirkListener getOnDisconnectListener(final Twirk twirk) {
        return new TwirkListener() {
            @Override
            public void onDisconnect() {
                try {
                    if (!twirk.connect()) {
                        twirk.close();
                    }
                } catch (IOException var2) {
                    twirk.close();
                } catch (InterruptedException e) {
                    LOG.error("Exception: {}", e.getMessage());
                    twirk.close();
                }
            }
        };
    }

    public void initializeSystem(Properties properties, Stage callingStage, boolean disable) throws IOException, InterruptedException {
        this.disable = disable;
        if (this.disable) {
            LOG.debug("Disabled twirk system");
            Platform.runLater(callingStage::show);
            Platform.runLater(() -> {
                InventoryController controller = (InventoryController) AESceneLoader.getInstance().getController(Scenes.S_INVENTORY);
                controller.disableTwirkMenus(false);
            });
            return;
        }
        String channel = properties.getProperty("twitch.channel");
        int attempts = 3;
        try {
            attempts = Integer.parseInt(properties.getProperty("twitch.connection.retryattempts"));
        } catch (NumberFormatException nfe) {
            LOG.warn("'{}' is not a number, defaulting to 3", properties.getProperty("twitch.connection.retryattempts"));
        }
        String channelFormatted = channel.substring(0, 1).toUpperCase() + channel.substring(1);
        twirk = (new TwirkBuilder(channel, channelFormatted, "oauth:" + properties.getProperty("twitch.bot.oauthtoken"))).setVerboseMode(false).build();
        twirk.addIrcListener(getOnDisconnectListener(twirk));
        twirk.addIrcListener(new ConvCommand(twirk, disable));
        twirk.addIrcListener(new VoteCommand(twirk, disable));
        twirk.addIrcListener(new ChangeVoteCommand(twirk, disable));
        twirk.addIrcListener(new ABPCommand(twirk, disable));
        twirk.addIrcListener(new QuestionCommand(twirk, disable));
        LOG.info("ArvBotPlus is loading");
        Thread.sleep(500L);
        int finalAttempts = attempts;
        new Thread(() -> {
            Platform.runLater(() -> {
                InventoryController controller = (InventoryController) AESceneLoader.getInstance().getController(Scenes.S_INVENTORY);
                controller.disableTwirkMenus(true);
            });
            boolean connection = false;
            for (int i = 0; i < finalAttempts; i++) {
                LOG.info("Trying to connect; {}/{}", (i + 1), finalAttempts);
                try {
                    connection = twirk.connect();
                } catch (IOException | InterruptedException e) {
                    LOG.error("Exception: {}", e.getMessage());
                }
                if (connection) break; //try to connect {attempts} times
            }
            if (!connection) {
                Platform.runLater(() -> {
                    SimplePopup.showPopupWarn("Connection to twitch failed. Please check your configuration and try again.");
                    callingStage.hide();
                });
            }
            Map<String, String> params = new HashMap<>();
            params.put("patternabp", MessageProperties.getString(MessageKey.TWIRK_PATTERN_PREFIX) + MessageProperties.getString(MessageKey.TWIRK_PATTERN_COMMAND_ABP));
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_STARTUP, params));
            Platform.runLater(callingStage::show);
            Platform.runLater(() -> {
                InventoryController controller = (InventoryController) AESceneLoader.getInstance().getController(Scenes.S_INVENTORY);
                controller.disableTwirkMenus(false);
            });

        }).start();
    }

    public void channelMessage(String message) {
        if (this.disable) {
            LOG.trace(message);
        } else {
            twirk.channelMessage(message);
        }
    }

    public void disconnect(boolean exit) {
        if (twirk != null && (!this.disable || this.twirk.isConnected())) {
            new Thread(() -> {
                twirk.disconnect();
                if (exit) Platform.exit();
                twirk = null;
                ((ConversionController) AESceneLoader.getInstance().getController(Scenes.S_CONV)).checkTwirk();
            }).start();
        }
    }

    public String getConnectedChannel() {
        if (!this.disable || (this.twirk != null && this.twirk.isConnected())) {
            return this.twirk.getNick();
        }
        return MessageProperties.getString(MessageKey.TWIRK_CONNECTION_NOTCONNECTED);
    }

    public boolean isNotLoaded() {
        return twirk == null || !twirk.isConnected();
    }
}
