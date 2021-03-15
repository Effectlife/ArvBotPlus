package be.effectlife.arvbotplus.twirk.commands;

import be.effectlife.arvbotplus.Main;
import com.gikk.twirk.Twirk;
import com.gikk.twirk.commands.CommandExampleBase;
import com.gikk.twirk.enums.USER_TYPE;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import be.effectlife.arvbotplus.twirk.conversions.data.*;

import java.util.Arrays;

public class ConvCommand extends CommandExampleBase {
    private static final String PATTERN = Main.PREFIX + "conv";
    private final Twirk twirk;

    public ConvCommand(Twirk twirk) {
        super(CommandType.CONTENT_COMMAND);
        this.twirk = twirk;
    }

    protected String getCommandWords() {
        return PATTERN;
    }

    protected USER_TYPE getMinUserPrevilidge() {
        return USER_TYPE.DEFAULT;
    }

    protected void performCommand(String command, TwitchUser sender, TwitchMessage message) {
        String content = message.getContent().trim();
        if(!content.startsWith(PATTERN))return;
        String messagecontent = content.replaceAll("( +)", " ").trim().toLowerCase();
        String[] split = messagecontent.split(" ");

        if (content.equals(PATTERN) ||
                (split.length > 1 && split[1].equals("options")) ||
                (split.length > 1 && split[1].equals("option")) ||
                (split.length > 1 && split[1].equals("help"))
        ) {
            //No additional params, help, option or options as param given. Print out instructions
            channelMessage("Hi " + sender.getDisplayName() + ", !conv uses the following syntax: " +
                    "\"!conv {category} {value} {sourceType} {targetType}\"");
            sendOptions();
            channelMessage("examples: '!conv temperature 30 c f ' or '!conv length 3 ft m '");
            return;
        }

        //try to parse the command

        if (split.length != 5) {
            System.out.println("oops, split is not 5 elements: " + Arrays.toString(split));
            channelMessage("Command was not valid, there should be exactly 4 arguments, but found " + (split.length - 1));
            return;
        }
        if (split[3].equals(split[4])) {
            channelMessage("Oopsie, both source and target types are the same...");
            return;
        }
        try {
            float sourceValue = Float.parseFloat(split[2]);

            switch (split[1]) {
                case "l":
                case "length":
                    Length lengthSource = Length.valueOf(split[3].toUpperCase());
                    Length lengthTarget = Length.valueOf(split[4].toUpperCase());
                    float lengthTargetValue = Conversion.convertLinear(sourceValue, lengthSource.getConversionToBase(), lengthTarget.getConversionToBase());
                    channelMessage("Hi " + sender.getDisplayName() + ", " + ((int) (sourceValue * 100f)) / 100f + lengthSource.toString().toLowerCase() + " is " + ((int) (lengthTargetValue * 100f)) / 100f + lengthTarget.toString().toLowerCase());
                    break;
                case "t":
                case "temp":
                case "temperature":
                    Temperature temperatureSource = Temperature.valueOf(split[3].toUpperCase());
                    Temperature temperatureTarget = Temperature.valueOf(split[4].toUpperCase());
                    float temperatureTargetValue = Conversion.convertTemp(sourceValue, temperatureSource, temperatureTarget);
                    channelMessage("Hi " + sender.getDisplayName() + ", " + ((int) (sourceValue * 100f)) / 100f + temperatureSource.toString().toLowerCase() + " is " + ((int) (temperatureTargetValue * 100f)) / 100f + temperatureTarget.toString().toLowerCase());
                    break;
                case "w":
                case "weight":
                    Weight weightSource = Weight.valueOf(split[3].toUpperCase());
                    Weight weightTarget = Weight.valueOf(split[4].toUpperCase());
                    float weightTargetValue = Conversion.convertLinear(sourceValue, weightSource.getConversionToBase(), weightTarget.getConversionToBase());
                    channelMessage("Hi " + sender.getDisplayName() + ", " + ((int) (sourceValue * 100f)) / 100f + weightSource.toString().toLowerCase() + " is " + ((int) (weightTargetValue * 100f)) / 100f + weightTarget.toString().toLowerCase());
                    break;
                case "v":
                case "volume":
                    Volume volumeSource = Volume.valueOf(split[3].toUpperCase());
                    Volume volumeTarget = Volume.valueOf(split[4].toUpperCase());
                    float volumeTargetValue = Conversion.convertLinear(sourceValue, volumeSource.getConversionToBase(), volumeTarget.getConversionToBase());
                    channelMessage("Hi " + sender.getDisplayName() + ", " + ((int) (sourceValue * 100f)) / 100f + volumeSource.toString().toLowerCase() + " is " + ((int) (volumeTargetValue * 100f)) / 100f + volumeTarget.toString().toLowerCase());
                    break;
                default:
                    channelMessage("Unknown category provided: '" + split[1] + "'. Please use one of the following options: ");
                    sendOptions();
                    break;
            }
        } catch (NumberFormatException e) {
            channelMessage("The value " + split[2] + " could not be parsed into a valid number. Please check if the number was entered correctly (may be a decimal dot or comma issue)");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("No enum constant")) {
                String[] exceptionMessage = e.getMessage().split("\\.");
                channelMessage(exceptionMessage[2] + " is not a valid " + exceptionMessage[1] + ". Please use one of the following options: ");
                sendOptions();
            }
        } catch (Exception e) {
            channelMessage("Oops, something went wrong internally. Please check the console logs for more info");
            e.printStackTrace();
        }
    }

    private void channelMessage(String message) {
        twirk.channelMessage(message);
    }

    private void sendOptions() {
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


        channelMessage("The following options are available: '" + options.toString() + "'");
    }
}
