package be.effectlife.arvbotplus.conversions;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import com.gikk.twirk.Twirk;
import com.gikk.twirk.TwirkBuilder;
import com.gikk.twirk.events.TwirkListener;
import be.effectlife.arvbotplus.conversions.commands.ConvCommand;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConversionBot {
    public ConversionBot() {
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        Properties properties = new Properties();
        String propFileName = "./config.properties";
        InputStream inputStream = new FileInputStream(propFileName);
        properties.load(inputStream);
        String channel = properties.getProperty("twitch.channel");
        Twirk twirk = (new TwirkBuilder(channel, "HARDCODED", "oauth:" + properties.getProperty("twitch.bot.oauthtoken"))).setVerboseMode(true).build();
        twirk.addIrcListener(getOnDisconnectListener(twirk));
        twirk.addIrcListener(new ConvCommand(twirk));
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
