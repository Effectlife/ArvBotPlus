package be.effectlife.arvbotplus.controllers.scenes;

import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.controllers.widgets.QuickPollWidgetController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.SceneContainer;
import be.effectlife.arvbotplus.loading.Scenes;
import be.effectlife.arvbotplus.utilities.Formatters;
import be.effectlife.arvbotplus.utilities.PollType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PollController implements IController {
    Logger LOG = LoggerFactory.getLogger(PollController.class);
    private PollType pollType = PollType.NONE;
    private QuickPollWidgetController quickPollWidget;

    @FXML
    private GridPane gpBase;

    @FXML
    private Button btnClearAll;

    @FXML
    private TextArea taQuestion;

    @FXML
    private TextField tfOptions;

    @FXML
    private Button btnOpenClosePoll;

    @FXML
    private Button btnLastCall;

    @FXML
    private VBox vboxPollOptions;

    @FXML
    void btnClearAll_Clicked(ActionEvent event) {

    }

    @FXML
    void btnLastCall_Clicked(ActionEvent event) {

    }

    @FXML
    void btnOpenClosePoll_Clicked(ActionEvent event) {

    }

    @FXML
    void tfOptions_Action(ActionEvent event) {
        LOG.info(tfOptions.getText());
    }

    @Override
    public void doInit() {
        SceneContainer quickPollContainer = AESceneLoader.getInstance().getSceneContainer(Scenes.W_QUICKPOLL);
        Region region = (Region) quickPollContainer.getScene().getRoot();
        setDefaultSize(region);
        gpBase.add(region, 0, 0);
        this.quickPollWidget = (QuickPollWidgetController) quickPollContainer.getController();
        //TODO: Add loading from save
        int options = 4;
        tfOptions.setTextFormatter(Formatters.NumbersOnly);
        tfOptions.focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal) PollController.this.tfOptions_Action(null);
                }
        );
        tfOptions.setText(options + "");
    }

    public PollType getPollType() {
        return pollType;
    }

    public void setPollType(PollType pollType) {
        this.pollType = pollType;
    }

    @Override
    public void onShow() {

    }

    @Override
    public void reloadView() {

    }

    public void startStopQuickPoll() {
        if (this.pollType == PollType.QUICK) {
            this.pollType = PollType.NONE;
        }else{
            this.pollType = PollType.QUICK;
        }
    }
}
