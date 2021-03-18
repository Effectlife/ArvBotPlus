package be.effectlife.arvbotplus.saves;

import be.effectlife.arvbotplus.controllers.IController;
import be.effectlife.arvbotplus.controllers.scenes.InventoryController;
import be.effectlife.arvbotplus.controllers.widgets.SkillWidgetController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.Scenes;
import be.effectlife.arvbotplus.saves.models.Save;
import be.effectlife.arvbotplus.saves.models.Skill;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SaveManager {
    private static Stage inventoryStage;

    public static void saveGame() {

        FileChooser fileChooser = new FileChooser();

        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Json files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(inventoryStage);

        if (file != null) {
            //File has been chosen
            //Compile all information about the inventory
            InventoryController controller = (InventoryController) AESceneLoader.getInstance().getController(Scenes.S_INVENTORY);
            String name = controller.getName();
            String itemsArtifacts = controller.getItemsArtifacts();
            String cluesNotes = controller.getCluesNotes();
            List<Skill> skills = controller.getSkills().stream().map(skill -> new Skill(skill.getSkillname(), skill.getType(), skill.getValue(), skill.getMaxValue(), skill.isUseColors())).collect(Collectors.toList());

            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                bw.write(new GsonBuilder().setPrettyPrinting().create().toJson(new Save(name, itemsArtifacts, cluesNotes, skills)));
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadGame() {

        FileChooser fileChooser = new FileChooser();

        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Json files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showOpenDialog(inventoryStage);

        if (file != null) {
            //File has been chosen
            //Compile all information about the inventory
            //TODO: add this functionality.
            try {
                Save save = new Gson().fromJson(new FileReader(file), Save.class);
                InventoryController controller = (InventoryController) AESceneLoader.getInstance().getController(Scenes.S_INVENTORY);
                controller.hardReset();
                controller.load(save);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setInventoryStage(Stage inventoryStage) {
        SaveManager.inventoryStage = inventoryStage;
    }
}
