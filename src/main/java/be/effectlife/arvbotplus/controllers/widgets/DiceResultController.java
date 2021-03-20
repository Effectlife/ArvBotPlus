package be.effectlife.arvbotplus.controllers.widgets;

import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.controllers.scenes.DiceController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.Scenes;
import be.effectlife.arvbotplus.utilities.ColorEnum;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class DiceResultController implements IController {
    private static final Logger LOG = LoggerFactory.getLogger(DiceResultController.class);
    @FXML
    private Text textResult;

    @FXML
    private VBox vbox;

    @FXML
    private Pane paneSep;

    @FXML
    private Text textTemplate;

    @FXML
    private WebView webView;


    @Override
    public void doInit() {

        textResult.wrappingWidthProperty().bind(vbox.widthProperty().subtract(15));
        textTemplate.wrappingWidthProperty().bind(vbox.widthProperty().subtract(15));
        webView.getChildrenUnmodifiable().addListener((ListChangeListener<Node>) change -> {
            Set<Node> scrolls = webView.lookupAll(".scroll-bar");
            for (Node scroll : scrolls) {
                scroll.setVisible(false);
            }
        });
    }

    public String loadStyle() {
        try {
            String crit = retrieveColor(ColorEnum.CRIT).toString().replace("0x", "#");
            String success = retrieveColor(ColorEnum.SUCCESS).toString().replace("0x", "#");
            String background = retrieveColor(ColorEnum.BACKGROUND).toString().replace("0x", "#");
            String text = retrieveColor(ColorEnum.TEXT).toString().replace("0x", "#");
            return String.format("<!DOCTYPE html><head><style>*{background-color:%s;text-align:center;font-family: Helvetica, Arial, Sans-Serif;}p{color:%s;}.color-crit{color:%s;}.color-success{color:%s;}</style></head>", background, text, crit, success);
        } catch (NullPointerException ignored) {
        }
        return "";
    }

    @Override
    public void onShow() {

    }

    @Override
    public void reloadView() {
    }

    public void setTextResult(String textResult) {
        this.textResult.setText(textResult);
    }

    public void setTextCalculation(String textCalculation) {
        try {
            webView.getEngine().loadContent(loadStyle() + "<body><div id=\"mydiv\">" + textCalculation + "</div></body>");
            webView.setPrefHeight(18);
            webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    double height = Double.parseDouble(webView.getEngine().executeScript("window.getComputedStyle(document.body, null).getPropertyValue('height')").toString().replace("px",""));
                    double height2 = Double.parseDouble(webView.getEngine().executeScript("window.getComputedStyle(document.body, null).getPropertyValue('height')").toString().replace("px","")); //Sometimes the height is retrieved way too high
//((HTMLDivElementImpl) webView.getEngine().getDocument().getElementById("mydiv")).getClientHeight();
                    Platform.runLater(() -> webView.setPrefHeight(Math.min(height, height2) + 18));
                }
            });
        } catch (NullPointerException | NumberFormatException ignored) {
            LOG.error("Oops: ", ignored);
        }

    }

    public void setTextTemplate(String textTemplate) {
        this.textTemplate.setText(textTemplate);
    }

    private Color retrieveColor(ColorEnum color) {
        DiceController diceController = (DiceController) AESceneLoader.getInstance().getController(Scenes.S_DICE);
        switch (color) {
            case CRIT:
                return diceController.getCritColor();
            case SUCCESS:
                return diceController.getSuccessColor();
            case BACKGROUND:
                return diceController.getBackgroundColor();
            case TEXT:
                return diceController.getTextColor();
        }
        return null;
    }

    public void reloadWidth(double newValue) {
        vbox.setPrefWidth(newValue);
        webView.setPrefWidth(newValue);
    }
}
