package be.effectlife.arvbotplus.saves;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

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
            //TODO: add this functionality.

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

        }
    }

    public static void setInventoryStage(Stage inventoryStage) {
        SaveManager.inventoryStage = inventoryStage;
    }
}
