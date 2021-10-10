package be.effectlife.arvbotplus.controllers.widgets;

import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.controllers.scenes.QuestionsController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.Scenes;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class QuestionWidgetController implements IController {
    private static final Logger LOG = LoggerFactory.getLogger(QuestionWidgetController.class);
    private int id;

    @FXML
    private TextArea textAreaQuestion;

    @FXML
    private Text textUsername;

    @FXML
    private Text textTimestamp;

    @FXML
    private Button buttonRead;

    @FXML
    private Button buttonRemove;

    @FXML
    private Text textQuestionCount;

    private boolean isAnswered = true;

    @Override
    public void doInit() {

    }

    @Override
    public void onShow() {

    }

    @Override
    public void reloadView() {

    }

    @FXML
    void onAnswered(ActionEvent event) {
        if (isAnswered) {
            //Set color to red
            buttonRead.getStyleClass().add("button-color-false");
            buttonRead.getStyleClass().remove("button-color-true");
        } else {
            //set color to green
            buttonRead.getStyleClass().add("button-color-true");
            buttonRead.getStyleClass().remove("button-color-false");
        }
        isAnswered = !isAnswered;
    }

    @FXML
    void onRemove(ActionEvent event) {
        ((QuestionsController) AESceneLoader.getInstance().getController(Scenes.S_QUESTIONS)).remove(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        textQuestionCount.setText("" + id);
        this.id = id;
    }

    public void setData(String displayName, String content, String timestamp, boolean answered) {
        textUsername.setText(displayName);
        textAreaQuestion.setText(content);
        textTimestamp.setText(timestamp);
        isAnswered = answered;
        onAnswered(null);
    }

    public String getUsername() {
        return textUsername.getText();
    }

    public String getTimestamp() {
        return textTimestamp.getText();
    }

    public String getQuestion() {
        return textAreaQuestion.getText();
    }

    public boolean isAnswered() {
        return isAnswered;
    }
}
