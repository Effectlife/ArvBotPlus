package be.effectlife.arvbotplus.loading;

import be.effectlife.arvbotplus.Main;
import be.effectlife.arvbotplus.models.twirkcommands.ABPCommand;
import be.effectlife.arvbotplus.models.twirkcommands.ChangeVoteCommand;
import be.effectlife.arvbotplus.models.twirkcommands.ConvCommand;
import be.effectlife.arvbotplus.models.twirkcommands.VoteCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageProperties {
    public static final String REPLACEHOLDER_OPEN = "" + (char) 30; //uses ascii Record separator
    public static final String REPLACEHOLDER_CLOSE = "" + (char) 31;//uses ascii Unit separator

    private static final Logger LOG = LoggerFactory.getLogger(MessageProperties.class);
    private static Properties properties;

    static {
        try {
            properties = loadProperties();
        } catch (IOException e) {
            LOG.error("Error happened", e);
        }
    }

    private MessageProperties() {
    }

    public static String getString(String key) {
        if (properties.containsKey(key)) {
            String property = properties.getProperty(key);
            property = property.replaceAll("\\{\\{\\{", REPLACEHOLDER_OPEN + "{");
            property = property.replaceAll("}}}", "}" + REPLACEHOLDER_CLOSE);
            property = property.replaceAll("\\{\\{", REPLACEHOLDER_OPEN);
            property = property.replaceAll("}}", REPLACEHOLDER_CLOSE);
            return property;
        }
        return key;
    }

    public static String getString(MessageKey key) {
        return getString(key.toString());
    }

    public static String getFixedBracketVersion(String uncleanString) {
        return uncleanString.replaceAll(REPLACEHOLDER_OPEN, "{").replaceAll(REPLACEHOLDER_CLOSE, "}");
    }

    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        String propFileName = "./strings.properties";
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(propFileName);
        } catch (FileNotFoundException e) {
            LOG.error("Cannot find properties file {}; Generating default file and exiting program.", propFileName);
            try {
                writePropertiesFile(propFileName);
            } catch (IOException ioException) {
                LOG.error("Error happened", ioException);
            }
            System.exit(1);

        }
        properties.load(inputStream);
        return properties;
    }

    /**
     * Generates the String for the MessageKey, and all replacements done
     *
     * @param key          the key of the message
     * @param replacements a map of replacements, any escapement is already handled.
     * @return The correctly generated String
     */
    public static String generateString(MessageKey key, Map<String, String> replacements) {
        StringBuilder stringBuilder = new StringBuilder(getString(key));
        replacements.put("patternabp", ABPCommand.PATTERN);
        replacements.put("patternchangevote", ChangeVoteCommand.PATTERN);
        replacements.put("patternvote", VoteCommand.PATTERN);
        replacements.put("patternconversion", ConvCommand.PATTERN);
        replacements.put("connectedchannel", Main.getTwirkSystem().getConnectedChannel());
        replacements.forEach((replaceKey, replaceValue) -> replaceAll(stringBuilder, "\\{" + replaceKey + "}", replaceValue));
        return getFixedBracketVersion(stringBuilder.toString());
    }

    private static void writePropertiesFile(String propFileName) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(propFileName))) {
            bw.write("#When editing this file, the app has to be restarted to see the changes. It gets loaded in at boot.\n" +
                    "#Also, no new lines are possible in this. This is due to the fact that proper new lines are not supported by twitch.\n" +
                    "\n" +
                    "scene.battle.button.add                        = Add\n" +
                    "scene.battle.button.remove                     = Remove\n" +
                    "scene.battle.button.clear                      = Clear\n" +
                    "scene.battle.text.val1                         = Skill\n" +
                    "scene.battle.text.val2                         = Stamina\n" +
                    "\n" +
                    "scene.dice.text.roll                           = Roll\n" +
                    "scene.dice.text.d                              = d\n" +
                    "scene.dice.button.roll                         = Roll\n" +
                    "\n" +
                    "scene.inventory.menu.file                      = File\n" +
                    "scene.inventory.menu.file.save                 = Save\n" +
                    "scene.inventory.menu.file.load                 = Load\n" +
                    "scene.inventory.menu.file.close                = Close\n" +
                    "scene.inventory.menu.tools                     = Tools\n" +
                    "scene.inventory.menu.tools.polls               = Polls\n" +
                    "scene.inventory.menu.tools.dice                = Dice Roller\n" +
                    "scene.inventory.menu.tools.battle              = Battle Manager\n" +
                    "scene.inventory.menu.help                      = Help\n" +
                    "scene.inventory.menu.help.about                = About\n" +
                    "scene.inventory.text.charname                  = Character's name:\n" +
                    "scene.inventory.text.placeholder               = Double click to change name\n" +
                    "scene.inventory.text.itemsandartifacts         = Items and Artifacts\n" +
                    "scene.inventory.text.cluesandnotes             = Clues and Notes\n" +
                    "\n" +
                    "scene.polls.text.connectedto                   = Connected to:\n" +
                    "scene.polls.defaultquestion                    = What should we do/say?\n" +
                    "scene.polls.text.options                       = Options\n" +
                    "scene.polls.button.clearall                    = Clear All\n" +
                    "scene.polls.button.open                        = Open\n" +
                    "scene.polls.button.close                       = Close\n" +
                    "scene.polls.button.lastcall                    = Last Call\n" +
                    "\n" +
                    "widget.polls.button.clear                      = Clear\n" +
                    "\n" +
                    "widget.quickpoll.button.clear                  = Clear\n" +
                    "widget.quickpoll.button.open                   = Open\n" +
                    "widget.quickpoll.button.close                  = Close\n" +
                    "widget.quickpoll.button.lastcall               = Last Call\n" +
                    "\n" +
                    "#These are all commands. these values are the command to be used in chat.\n" +
                    "#The prefix can also be set here. Recommended to use a single character, multiple characters should work, but are not guaranteed to.\n" +
                    "twirk.pattern.prefix                           = $\n" +
                    "twirk.pattern.command.abp                      = abp\n" +
                    "twirk.pattern.command.changevote               = changevote\n" +
                    "twirk.pattern.command.vote                     = vote\n" +
                    "twirk.pattern.command.conversion               = conv\n" +
                    "\n" +
                    "#words in curly brackets {example} are placeholders. These are replaced at runtime.\n" +
                    "#Any text in double curly brackets, are printed as is with one bracket surrounding it. \"{{example}}\" gets printed as \"{example}\"\n" +
                    "#If there are 3 brackets, the placeholder value will be printed in brackets. For example: when voting for option 2, \"{{{votevalue}}}\" gets printed as \"{2}\"\n" +
                    "#More than 3 brackets will cause issues in the printed text.\n" +
                    "\n" +
                    "#This is a list with all globally available placeholders:\n" +
                    "#   {patternabp}            -> The pattern used to trigger the abp command, including prefix\n" +
                    "#   {patternchangevote}     -> The pattern used to trigger the changevote command, including prefix\n" +
                    "#   {patternvote}           -> The pattern used to trigger the vote command, including prefix\n" +
                    "#   {patternconversion}     -> The pattern used to trigger the conversion command, including prefix\n" +
                    "#   {connectedchannel}      -> The name of the channel that the bot is currently connected to (streamer)\n" +
                    "\n" +
                    "#Available in all commands\n" +
                    "#   {pattern}               -> The pattern for the currently triggered command, including prefix\n" +
                    "#   {sender}                -> The chat user who triggered the command (eg. the voter)\n" +
                    "\n" +
                    "#Only available for 'vote' and 'changevote'\n" +
                    "#   {votevalue}             -> The value a person in chat has tried to vote for. This is before validating a valid vote\n" +
                    "#   {votecount}             -> The amount of votes an option got when closing a poll\n" +
                    "\n" +
                    "#Only available for 'conversion'\n" +
                    "#   {sourcevalue}           -> The value of the source type. Eg: '3' (Only available for 'conversion')\n" +
                    "#   {sourcetype}            -> The short identifier of the source type Eg: 'm' (Only available for 'conversion')\n" +
                    "#   {targetvalue}           -> The value of the target type. Eg: '9.84' (Only available for 'conversion')\n" +
                    "#   {targettype}            -> The short identifier of the target type Eg. 'ft' (Only available for 'conversion')\n" +
                    "#   {conversionoptions}     -> The options that can be used for conversions: Eg. 'l/length (m, ft); t/temp/temperature (f, c);' (Only available for 'conversion')\n" +
                    "#   {count}                 -> The amount of parameters given to the conversion command.\n" +
                    "\n" +
                    "twirk.message.startup                          = ArvBotPlus has loaded. Use {patternabp} to see available commands\n" +
                    "\n" +
                    "#   {commands}              -> These are all commands that are available to the twitch bot.\n" +
                    "twirk.message.abp.help                         = Available commands are: {commands} Use each command without additional parameters to see more information about the command.\n" +
                    "\n" +
                    "twirk.message.poll.template.question           = {question} | Vote using {patternvote} {{option}} for: {options}\n" +
                    "twirk.message.poll.template.draw               = Poll closed | There was a draw with {votecount} vote(s) between the following options: {options}\n" +
                    "twirk.message.poll.template.win                = Poll closed | The following option has won with {votecount} vote(s): {option}\n" +
                    "twirk.message.quickpoll.template.question      = QuickPoll started | Vote using '{patternvote} 1' or {patternvote} 2'.\n" +
                    "twirk.message.quickpoll.template.draw          = QuickPoll closed | There was a draw with {votecount} vote(s) between both options.\n" +
                    "twirk.message.quickpoll.template.win           = QuickPoll closed | Option {option} has won with {votecount} vote(s).\n" +
                    "\n" +
                    "twirk.message.vote.help                        = Hi {sender}, {pattern} uses the following syntax: \"{pattern} {{option}}\"; To change your vote, use \"{patternchangevote} {{option}}\".\n" +
                    "twirk.message.vote.alreadyvoted                = You already voted, {sender}; To change your vote, use \"{patternchangevote} {{option}}.\n" +
                    "twirk.message.vote.addedvote                   = Thanks for voting for {{{votevalue}}}, {sender}.\n" +
                    "twirk.message.vote.invalidvote                 = Sorry {sender}, {{{votevalue}}} is not a valid option. Please try again.\n" +
                    "twirk.message.vote.nopollactive                = There is currently no poll active.\n" +
                    "twirk.message.vote.lastcall                    = Last Call!\n" +
                    "\n" +
                    "twirk.message.changevote.help                  = Hi {sender}, {pattern} uses the following syntax: \"{pattern} {{option}}\"; If you haven't voted yet, use \"{patternvote} {{option}}\" to cast your vote.\n" +
                    "twirk.message.changevote.alreadyvoted          = You already voted for {{{votevalue}}}, {sender}.\n" +
                    "twirk.message.changevote.addedvote             = Thanks for changing your vote to {{{votevalue}}}, {sender}.\n" +
                    "twirk.message.changevote.notyetvoted           = You haven't voted yet, {sender}, please vote first with {patternvote} before trying to change it.\n" +
                    "twirk.message.changevote.invalidvote           = Sorry {sender}, {{{votevalue}}} is not a valid option. Please try again.\n" +
                    "twirk.message.changevote.nopollactive          = There is currently no poll active.\n" +
                    "\n" +
                    "twirk.message.conversion.help                  = Hi {sender}, {pattern} uses the following syntax: \"!conv {{category}} {{value}} {{sourceType}} {{targetType}}\"\n" +
                    "twirk.message.conversion.example               = Examples: \"{pattern} temperature 30 c f\" or \"{pattern} length 3 ft m\"\n" +
                    "twirk.message.conversion.invalidargumentamount = Command was not valid, there should be exactly 4 arguments ({{category}} {{value}} {{sourceType}} {{targetType}}), but found {count}\n" +
                    "twirk.message.conversion.sametypes             = Oopsie, both source and target types are the same...\n" +
                    "twirk.message.conversion.result                = Hi {sender}, {sourcevalue}{sourcetype} is {targetvalue}{targettype}.\n" +
                    "twirk.message.conversion.unknowncategory       = Unknown category provided: \"{category}\", please use one of the following options:\n" +
                    "twirk.message.conversion.notanumber            = The value {value} could not be parsed into a valid number. Please check if the number was entered correctly.\n" +
                    "twirk.message.conversion.invalidtype           = {type} is not a valid {category}. Please use one of the following options:\n" +
                    "twirk.message.conversion.generalerror          = Oops, something went wrong internally. {connectedchannel}, please check the console logs for more info.\n" +
                    "twirk.message.conversion.options               = The following options are available: \"{conversionoptions}\".\n");
            bw.flush();
        }
    }

    private static void replaceAll(StringBuilder sb, String find, String replace) {

        //compile pattern from find string
        Pattern p = Pattern.compile(find);

        //create new Matcher from StringBuilder object
        Matcher matcher = p.matcher(sb);

        //index of StringBuilder from where search should begin
        int startIndex = 0;

        while (matcher.find(startIndex)) {

            sb.replace(matcher.start(), matcher.end(), replace);

            //set next start index as start of the last match + length of replacement
            startIndex = matcher.start() + replace.length();
        }
    }

    public static List<String> getAllSiblings(MessageKey source) {
        String parent = source.toString().substring(0, source.toString().lastIndexOf('.'));
        List<String> siblings = new ArrayList<>();
        for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements(); ) {
            String name = (String) e.nextElement();
            if (name.startsWith(parent)) siblings.add(properties.getProperty(name));
        }
        return siblings;
    }
}
