package be.effectlife.arvbotplus.controllers.scenes;

import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.controllers.widgets.QuestionWidgetController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.SceneContainer;
import be.effectlife.arvbotplus.loading.Scenes;
import be.effectlife.arvbotplus.saves.SaveManager;
import be.effectlife.arvbotplus.saves.models.QuestionSave;
import be.effectlife.arvbotplus.utilities.SimplePopup;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class QuestionsController implements IController {
    private static final Logger LOG = LoggerFactory.getLogger(QuestionsController.class);

    @FXML
    private Text textQuestionCounter;

    @FXML
    private VBox vboxQuestions;

    private List<QuestionWidgetController> questionsList;
    private int questionCounter = 0;

    @FXML
    void btnLoadClicked(ActionEvent event) {
        SaveManager.loadQuestions();
    }

    @FXML
    void btnSaveClicked(ActionEvent event) {
        SaveManager.saveQuestions();
    }


    @Override
    public void doInit() {
        questionsList = new ArrayList<>();
        reloadView();
    }


    @Override
    public void reloadView() {
        vboxQuestions.getChildren().clear();
        questionsList.forEach(enemyController -> vboxQuestions.getChildren().add(AESceneLoader.getInstance().getScene(Scenes.W_QUESTION, "_" + enemyController.getId()).getRoot()));
        textQuestionCounter.setText("Questions: " + questionsList.size());
    }

    public void addQuestion(String displayName, String content, String timestamp) {
        addQuestion(displayName, content, timestamp, true);
    }

    public void addQuestion(String displayName, String content, String timestamp, boolean answered) {
        questionCounter++;
        createWidget(questionCounter, displayName, content, timestamp, answered);
        reloadView();
    }

    private void createWidget(int id, String displayName, String content, String timestamp, boolean answered) {
        SceneContainer sceneContainer = AESceneLoader.getInstance().getSceneContainer(Scenes.W_QUESTION, "_" + id);
        QuestionWidgetController questionWidgetController = (QuestionWidgetController) sceneContainer.getController();
        questionsList.add(questionWidgetController);
        vboxQuestions.getChildren().add(sceneContainer.getScene().getRoot());
        sceneContainer.getScene().getRoot().setUserData(id);
        questionWidgetController.setData(displayName, content, timestamp, answered);
        questionWidgetController.setId(id);
        questionWidgetController.reloadView();
    }

    public void remove(int id) {
        Optional<QuestionWidgetController> first = questionsList.stream().filter(question -> question.getId() == id).findFirst();
        first.ifPresent(questionWidgetController -> {
            for (Node child : vboxQuestions.getChildren()) {
                if (Integer.parseInt(child.getUserData().toString()) == questionWidgetController.getId()) {
                    vboxQuestions.getChildren().remove(child);
                    questionsList.remove(questionWidgetController);
                    break;
                }
            }
        });
        reloadView();
    }

    public List<QuestionWidgetController> getQuestions() {
        return questionsList;
    }

    public void load(QuestionSave questionSave) {
        try {
            questionsList.stream().map(QuestionWidgetController::getId).collect(Collectors.toList()).forEach(this::remove);
            questionCounter = 0;
            questionSave.getQuestions().forEach((questionSaveItem -> addQuestion(questionSaveItem.getUsername(), questionSaveItem.getQuestion(), questionSaveItem.getTimestamp(), questionSaveItem.isAnswered())));
        } catch (Exception e) {
            SimplePopup.showPopupError("File cannot be loaded, are you sure it is a Questions save?\n\nException: " + e.getMessage() + "\n" + e.getStackTrace()[0]);
            LOG.error("File cannot be loaded", e);
        }
    }
}
