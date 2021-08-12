package be.effectlife.arvbotplus.controllers.scenes;

import be.effectlife.arvbotplus.Main;
import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.controllers.widgets.QuestionWidgetController;
import be.effectlife.arvbotplus.controllers.widgets.QuestionWidgetController;
import be.effectlife.arvbotplus.controllers.widgets.QuestionWidgetController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.SceneContainer;
import be.effectlife.arvbotplus.loading.Scenes;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
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

    @Override
    public void doInit() {
        questionsList = new ArrayList<>();
        reloadView();
    }

    @Override
    public void onShow() {

    }

    @Override
    public void reloadView() {
        vboxQuestions.getChildren().clear();
        questionsList.forEach(enemyController -> vboxQuestions.getChildren().add(AESceneLoader.getInstance().getScene(Scenes.W_QUESTION, "_" + enemyController.getId()).getRoot()));
        textQuestionCounter.setText("Questions: " + questionsList.size());
    }

    public void addQuestion(String displayName, String content, LocalTime timestamp) {
        questionCounter++;
        createWidget(questionCounter, displayName, content, timestamp);
        reloadView();
    }

    private void createWidget(int id, String displayName, String content, LocalTime timestamp) {
        SceneContainer sceneContainer = AESceneLoader.getInstance().getSceneContainer(Scenes.W_QUESTION, "_" + id);
        QuestionWidgetController questionWidgetController = (QuestionWidgetController) sceneContainer.getController();
        questionsList.add(questionWidgetController);
        vboxQuestions.getChildren().add(sceneContainer.getScene().getRoot());
        sceneContainer.getScene().getRoot().setUserData(id);
        questionWidgetController.setData(displayName, content, timestamp);
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
}
