package be.effectlife.arvbotplus.twirk.commands;

import be.effectlife.arvbotplus.Main;
import be.effectlife.arvbotplus.loading.MessageKey;
import be.effectlife.arvbotplus.loading.MessageProperties;
import com.gikk.twirk.Twirk;
import com.gikk.twirk.commands.CommandExampleBase;
import com.gikk.twirk.enums.USER_TYPE;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import be.effectlife.arvbotplus.twirk.conversions.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConvCommand extends CommandExampleBase {
    private static final Logger LOG = LoggerFactory.getLogger(ConvCommand.class);
    public static final String PATTERN = MessageProperties.getString(MessageKey.TWIRK_PATTERN_PREFIX) + MessageProperties.getString(MessageKey.TWIRK_PATTERN_COMMAND_CONVERSION);
    private final Twirk twirk;
    private final boolean disable;

    public ConvCommand(Twirk twirk, boolean disable) {
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
        String messagecontent = content.replaceAll("( +)", " ").trim().toLowerCase();
        String[] split = messagecontent.split(" ");
        Map<String, String> params = new HashMap<>();
        params.put("sender", sender.getDisplayName());
        params.put("pattern", PATTERN);
        params.put("count", split.length + "");
        if (content.equals(PATTERN) ||
                (split.length > 1 && split[1].equals("options")) ||
                (split.length > 1 && split[1].equals("option")) ||
                (split.length > 1 && split[1].equals("help"))
        ) {
            //No additional params, help, option or options as param given. Print out instructions
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CONVERSION_HELP, params));
            sendOptions(params);
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CONVERSION_EXAMPLE, params));
            return;
        }

        //try to parse the command

        if (split.length != 5) {
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CONVERSION_INVALIDARGUMENTAMOUNT, params));
            return;
        }
        if (split[3].equals(split[4])) {
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CONVERSION_SAMETYPES, params));
            return;
        }
        try {
            float sourceValue = Float.parseFloat(split[2].replaceAll(",", "."));

            switch (split[1]) {
                case "l":
                case "length":
                    Length lengthSource = Length.valueOf(split[3].toUpperCase());
                    Length lengthTarget = Length.valueOf(split[4].toUpperCase());
                    float lengthTargetValue = Conversion.convertLinear(sourceValue, lengthSource.getConversionToBase(), lengthTarget.getConversionToBase());
                    params.put("sourcevalue", (((int) (sourceValue * 100f)) / 100f) + "");
                    params.put("sourcetype", lengthSource.toString().toLowerCase());
                    params.put("targetvalue", (((int) (lengthTargetValue * 100f)) / 100f) + "");
                    params.put("targettype", lengthTarget.toString().toLowerCase());
                    channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CONVERSION_RESULT, params));
                    break;
                case "t":
                case "temp":
                case "temperature":
                    Temperature temperatureSource = Temperature.valueOf(split[3].toUpperCase());
                    Temperature temperatureTarget = Temperature.valueOf(split[4].toUpperCase());
                    float temperatureTargetValue = Conversion.convertTemp(sourceValue, temperatureSource, temperatureTarget);
                    params.put("sourcevalue", (((int) (sourceValue * 100f)) / 100f) + "");
                    params.put("sourcetype", temperatureSource.toString().toLowerCase());
                    params.put("targetvalue", (((int) (temperatureTargetValue * 100f)) / 100f) + "");
                    params.put("targettype", temperatureTarget.toString().toLowerCase());
                    channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CONVERSION_RESULT, params));
                    break;
                case "w":
                case "weight":
                    Weight weightSource = Weight.valueOf(split[3].toUpperCase());
                    Weight weightTarget = Weight.valueOf(split[4].toUpperCase());
                    float weightTargetValue = Conversion.convertLinear(sourceValue, weightSource.getConversionToBase(), weightTarget.getConversionToBase());
                    params.put("sourcevalue", (((int) (sourceValue * 100f)) / 100f) + "");
                    params.put("sourcetype", weightSource.toString().toLowerCase());
                    params.put("targetvalue", (((int) (weightTargetValue * 100f)) / 100f) + "");
                    params.put("targettype", weightTarget.toString().toLowerCase());
                    channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CONVERSION_RESULT, params));
                    break;
                case "v":
                case "volume":
                    Volume volumeSource = Volume.valueOf(split[3].toUpperCase());
                    Volume volumeTarget = Volume.valueOf(split[4].toUpperCase());
                    float volumeTargetValue = Conversion.convertLinear(sourceValue, volumeSource.getConversionToBase(), volumeTarget.getConversionToBase());
                    params.put("sourcevalue", (((int) (sourceValue * 100f)) / 100f) + "");
                    params.put("sourcetype", volumeSource.toString().toLowerCase());
                    params.put("targetvalue", (((int) (volumeTargetValue * 100f)) / 100f) + "");
                    params.put("targettype", volumeTarget.toString().toLowerCase());
                    channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CONVERSION_RESULT, params));
                    break;
                default:
                    params.put("category", split[1]);
                    channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CONVERSION_UNKNOWNCATEGORY, params));
                    sendOptions(params);
                    break;
            }
        } catch (NumberFormatException e) {
            params.put("value", split[2]);
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CONVERSION_NOTANUMBER, params));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("No enum constant")) {
                String[] exceptionMessage = e.getMessage().split("\\.");
                params.put("category", exceptionMessage[1]);
                params.put("type", exceptionMessage[2]);
                channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CONVERSION_INVALIDTYPE, params));
                sendOptions(params);
            }
        } catch (Exception e) {
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CONVERSION_GENERALERROR, params));
            e.printStackTrace();
        }
    }

    private void channelMessage(String message) {
        if (this.disable) {
            LOG.trace(message);
        } else {
            twirk.channelMessage(message);
        }
    }

    private void sendOptions(Map<String, String> params) {

        StringBuilder options = new StringBuilder();
        options.append("l/length (");
        Length[] lengthValues = Length.values();
        for (int i = 0; i < lengthValues.length; i++) {
            Length value = lengthValues[i];
            options.append(value.toString().toLowerCase());
            if (i != lengthValues.length - 1) {
                options.append(", ");
            }
        }
        options.append("); t/temp/temperature (");
        Temperature[] temperatureValue = Temperature.values();
        for (int i = 0; i < temperatureValue.length; i++) {
            Temperature value = temperatureValue[i];
            options.append(value.toString().toLowerCase());
            if (i != temperatureValue.length - 1) {
                options.append(", ");
            }
        }
        options.append("); w/weight (");
        Weight[] weightValue = Weight.values();
        for (int i = 0; i < weightValue.length; i++) {
            Weight value = weightValue[i];
            options.append(value.toString().toLowerCase());
            if (i != weightValue.length - 1) {
                options.append(", ");
            }
        }
        options.append("); v/volume (");
        Volume[] volumeValue = Volume.values();
        for (int i = 0; i < volumeValue.length; i++) {
            Volume value = volumeValue[i];
            options.append(value.toString().toLowerCase());
            if (i != volumeValue.length - 1) {
                options.append(", ");
            }
        }
        options.append("); ");

        params.put("options", options.toString());
        channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CONVERSION_OPTIONS, params));

    }
}
