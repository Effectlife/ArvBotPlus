package be.effectlife.arvbotplus.models.twirkcommands;

import be.effectlife.arvbotplus.loading.MessageKey;
import be.effectlife.arvbotplus.loading.MessageProperties;
import be.effectlife.arvbotplus.models.conversions.*;
import be.effectlife.arvbotplus.services.ConversionService;
import com.gikk.twirk.Twirk;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConvCommand extends BaseCommand {
    private static final Logger LOG = LoggerFactory.getLogger(ConvCommand.class);
    private final ConversionService conversionService;
    public static final String PATTERN = MessageProperties.getString(MessageKey.TWIRK_PATTERN_PREFIX) + MessageProperties.getString(MessageKey.TWIRK_PATTERN_COMMAND_CONVERSION);

    public ConvCommand(Twirk twirk, boolean disable) {
        super(CommandType.CONTENT_COMMAND, twirk, disable);
        conversionService = new ConversionService();
    }

    protected String getCommandWords() {
        return PATTERN;
    }

    protected void handleCommand(String content, TwitchUser sender, TwitchMessage message) {
        String messageContent = content.replaceAll("( +)", " ").trim().toLowerCase();
        String[] split = messageContent.split(" ");
        Map<String, String> params = new HashMap<>();
        params.put("sender", sender.getDisplayName());
        params.put("pattern", PATTERN);
        params.put("count", split.length - 1 + ""); //-1 to remove pattern
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

        if (split.length != 4) {
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CONVERSION_INVALIDARGUMENTAMOUNT, params));
            return;
        }
        if (split[2].equals(split[3])) {
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CONVERSION_SAMETYPES, params));
            return;
        }
        handleConversion(split, params);
    }

    private void handleConversion(String[] split, Map<String, String> params) {
        try {
            float sourceValue = Float.parseFloat(split[1].replaceAll(",", "."));
            CType sourceUnitType = conversionService.getCTypeFromString(split[2]);
            CType targetUnitType = conversionService.getCTypeFromString(split[3]);

            if (sourceUnitType == null) {
                channelMessage(split[2] + " cannot be parsed into a valid unit.");
                return;
            }

            if (targetUnitType == null) {
                channelMessage(split[3] + " cannot be parsed into a valid unit.");
                return;
            }

            ConversionResult convert = conversionService.convert(sourceValue, sourceUnitType, targetUnitType);
            if (convert == null) {
                channelMessage("Cannot convert, source type " + sourceUnitType.getUnit() + "(" + sourceUnitType.getClass().getSimpleName() + ") and target type " + targetUnitType.getUnit() + "(" + targetUnitType.getClass().getSimpleName() + ") are not of the same unit type");
                return;
            }
            params.put("sourcevalue", (((int) (sourceValue * 100f)) / 100f) + "");
            params.put("sourcetype", sourceUnitType.getDisplayName());
            params.put("targetvalue", (((int) (convert.getTargetValue() * 100f)) / 100f) + "");
            params.put("targettype", targetUnitType.getDisplayName());
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CONVERSION_RESULT, params));
        } catch (NumberFormatException e) {
            params.put("value", split[1]);
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CONVERSION_NOTANUMBER, params));
        } catch (Exception e) {
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CONVERSION_GENERALERROR, params));
            LOG.error("Error happened", e);
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
        options.append("Length/Distance (");
        formatCType(options, Length.getAllValues());
        options.append("); Temperature (");
        formatCType(options, Temperature.getAllValues());
        options.append("); Weight (");
        formatCType(options, Weight.getAllValues());
        options.append("); Volume (");
        formatCType(options, Volume.getAllValues());
        options.append("); ");

        params.put("options", options.toString());
        channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CONVERSION_OPTIONS, params));

    }

    private void formatCType(StringBuilder options, List<CType> weightValue) {
        for (int i = 0; i < weightValue.size(); i++) {
            CType value = weightValue.get(i);
            options.append(value.toString().toLowerCase());
            if (i != weightValue.size() - 1) {
                options.append(", ");
            }
        }
    }
}
