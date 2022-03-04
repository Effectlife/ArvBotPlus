package be.effectlife.arvbotplus.twirk.commands;

import be.effectlife.arvbotplus.controllers.scenes.QuestionsController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.MessageKey;
import be.effectlife.arvbotplus.loading.MessageProperties;
import be.effectlife.arvbotplus.loading.Scenes;
import com.gikk.twirk.Twirk;
import com.gikk.twirk.commands.CommandExampleBase;
import com.gikk.twirk.enums.USER_TYPE;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class QuestionCommand extends CommandExampleBase {
    public static final String PATTERN = MessageProperties.getString(MessageKey.TWIRK_PATTERN_PREFIX) + MessageProperties.getString(MessageKey.TWIRK_PATTERN_COMMAND_QUESTION);
    private static final Logger LOG = LoggerFactory.getLogger(QuestionCommand.class);
    private final Twirk twirk;
    private final boolean disable;

    public QuestionCommand(Twirk twirk, boolean disable) {
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
        String contentFiltered = content.replace(PATTERN, "").trim();
        HashMap<String, String> options = new HashMap<>();
        options.put("pattern", PATTERN);
        options.put("sender", sender.getDisplayName());
        options.put("question", contentFiltered);
        if (message.getContent().trim().equals(PATTERN)) {
            //return instructions
            channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_QUESTION_HELP, options));
            return;
        }
        Platform.runLater(() -> generateQuestionWidget(sender.getDisplayName(), contentFiltered, LocalTime.now().format(DateTimeFormatter.ofPattern("h:mma"))));
        channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_QUESTION_CONFIRMATION, options));
    }

    private void generateQuestionWidget(String displayName, String content, String timestamp) {
        QuestionsController questionsController = (QuestionsController) AESceneLoader.getInstance().getController(Scenes.S_QUESTIONS);
        questionsController.addQuestion(displayName, content, timestamp);
    }


    private void channelMessage(String message) {
        if (this.disable) {
            LOG.trace(message);
        } else {
            twirk.channelMessage(message);
        }
    }
}
