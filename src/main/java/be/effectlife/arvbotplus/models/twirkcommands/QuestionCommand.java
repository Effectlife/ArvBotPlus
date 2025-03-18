package be.effectlife.arvbotplus.models.twirkcommands;

import be.effectlife.arvbotplus.controllers.scenes.QuestionsController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.MessageKey;
import be.effectlife.arvbotplus.loading.MessageProperties;
import be.effectlife.arvbotplus.loading.Scenes;
import com.gikk.twirk.Twirk;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class QuestionCommand extends BaseCommand {
    public static final String PATTERN = MessageProperties.getString(MessageKey.TWIRK_PATTERN_PREFIX) + MessageProperties.getString(MessageKey.TWIRK_PATTERN_COMMAND_QUESTION);

    public QuestionCommand(Twirk twirk, boolean disable) {
        super(CommandType.CONTENT_COMMAND, twirk, disable);
        LOG = LoggerFactory.getLogger(QuestionCommand.class);
    }

    protected String getCommandWords() {
        return PATTERN;
    }

    protected void handleCommand(String content, TwitchUser sender, TwitchMessage message) {
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
}
