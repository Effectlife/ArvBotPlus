package be.effectlife.arvbotplus.controllers.scenes;

import be.effectlife.arvbotplus.Main;
import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.controllers.widgets.SkillWidgetController;
import be.effectlife.arvbotplus.loading.*;
import be.effectlife.arvbotplus.saves.SaveManager;
import be.effectlife.arvbotplus.saves.models.Save;
import be.effectlife.arvbotplus.saves.models.Skill;
import be.effectlife.arvbotplus.services.ConversionResult;
import be.effectlife.arvbotplus.services.ConversionService;
import be.effectlife.arvbotplus.services.conversions.data.CType;
import be.effectlife.arvbotplus.twirk.TwirkSystem;
import be.effectlife.arvbotplus.utilities.JFXExtensions;
import be.effectlife.arvbotplus.utilities.SimplePopup;
import be.effectlife.arvbotplus.utilities.SkillType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static be.effectlife.arvbotplus.Main.twirkSystem;


public class ConversionController implements IController {
    private static final Logger LOG = LoggerFactory.getLogger(ConversionController.class);
    ConversionService conversionService;
    private HashMap<String, List<CType>> dropdowns;

    {
        dropdowns = CType.getAllAvailableTypes();
        conversionService = new ConversionService();
    }

    @FXML
    private GridPane base;

    @FXML
    private ComboBox<String> comboCategory;

    @FXML
    private ComboBox<CType> comboSourceType;

    @FXML
    private ComboBox<CType> comboTargetType;

    @FXML
    private TextField tfSource;

    @FXML
    private TextField tfTarget;

    @FXML
    private Button btnSend;

    @FXML
    void btnSend_Clicked(ActionEvent event) {
        try {
            float sourceVal = StringUtils.isBlank(tfSource.getText()) ? 0 : Float.parseFloat(tfSource.getText());
            Map<String, String> params = new HashMap<>();
            params.put("sender", twirkSystem.getConnectedChannel());
            params.put("sourcevalue", (((int) (sourceVal * 100f)) / 100f) + "");
            params.put("sourcetype", comboSourceType.getValue().getUnit());
            params.put("targetvalue", tfTarget.getText());
            params.put("targettype", comboTargetType.getValue().getUnit());
            twirkSystem.channelMessage(MessageProperties.generateString(MessageKey.TWIRK_MESSAGE_CONVERSION_RESULT, params));
        } catch (NumberFormatException nfe) {
            LOG.error("Could not convert: " + nfe.getMessage());
        }
    }

    @FXML
    void tfSource_Confirmed(ActionEvent event) {
        updateCalculation();
    }

    @FXML
    void cbTypeChanged(ActionEvent event) {
        updateCalculation();
    }

    @Override
    public void doInit() {
        ObservableList<String> typesList = FXCollections.observableArrayList(dropdowns.keySet().stream().sorted().collect(Collectors.toList()));
        comboCategory.setItems(typesList);
        comboCategory.getSelectionModel().select(0);
        updateTypeComboBoxes();
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            if (text.matches("[0-9,.]*")) {
                return change;
            }
            return null;
        };
        tfSource.setTextFormatter(new TextFormatter<>(filter));

        tfSource.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue))
                updateCalculation();
        }));
        checkTwirk();
    }

    public void checkTwirk() {
        if (twirkSystem == null || !twirkSystem.isLoaded()) {
            btnSend.setDisable(true);
            btnSend.setTooltip(new Tooltip("Twitch bot is not loaded."));
        } else {
            btnSend.setDisable(false);
            btnSend.setTooltip(null);
        }
    }


    public void comboboxCategory_Changed(ActionEvent event) {
        updateTypeComboBoxes();
        updateCalculation();
    }

    private void updateTypeComboBoxes() {
        try {
            for (Map.Entry<String, List<CType>> entry : dropdowns.entrySet()) {
                String key = entry.getKey();
                List<CType> value = entry.getValue();
                if (key.equals(comboCategory.getValue())) {
                    comboSourceType.setItems(FXCollections.observableArrayList(value.stream().sorted(Comparator.comparing(CType::toString)).collect(Collectors.toList())));
                    comboTargetType.setItems(FXCollections.observableArrayList(value.stream().sorted(Comparator.comparing(CType::toString)).collect(Collectors.toList())));
                    break;
                }
            }
            comboSourceType.getSelectionModel().select(0);
            comboTargetType.getSelectionModel().select(1);
        } catch (RuntimeException ignored) {

        }
    }

    private void updateCalculation() {
        String text = tfSource.getText();
        ConversionResult convert;
        try {
            float sourceVal = StringUtils.isBlank(text) ? 0 : Float.parseFloat(text);
            convert = conversionService.convert(sourceVal, comboSourceType.getValue(), comboTargetType.getValue());
        } catch (NumberFormatException nfe) {
            LOG.error("Could not convert: " + nfe.getMessage());
            return;
        }
        if (convert != null) {
            tfTarget.setText((((int) (convert.getTargetValue() * 100f)) / 100f) + "");
        } else {
            tfTarget.setText("0");
        }
    }

    @Override
    public void onShow() {

    }

    @Override
    public void reloadView() {

    }
}
