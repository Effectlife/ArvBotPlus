package be.effectlife.arvbotplus.saves;

import be.effectlife.arvbotplus.controllers.scenes.InventoryController;
import be.effectlife.arvbotplus.controllers.scenes.PollController;
import be.effectlife.arvbotplus.controllers.scenes.QuestionsController;
import be.effectlife.arvbotplus.loading.AESceneLoader;
import be.effectlife.arvbotplus.loading.Scenes;
import be.effectlife.arvbotplus.saves.models.GameSave;
import be.effectlife.arvbotplus.saves.models.PollSave;
import be.effectlife.arvbotplus.saves.models.QuestionSave;
import be.effectlife.arvbotplus.saves.models.Skill;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SaveManager {
    private static Stage inventoryStage;
    private static Stage pollStage;
    private static Stage questionsStage;

    private static String lastFolder;

    public static void saveGame() {

        FileChooser fileChooser = getFileChooser();

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
                bw.write(new GsonBuilder().setPrettyPrinting().create().toJson(new GameSave(name, itemsArtifacts, cluesNotes, skills)));
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            lastFolder = file.getParent();
        }
    }

    public static void savePoll() {

        FileChooser fileChooser = getFileChooser();

        //Show save file dialog
        File file = fileChooser.showSaveDialog(pollStage);

        if (file != null) {
            //File has been chosen
            //Compile all information about the inventory
            PollController controller = (PollController) AESceneLoader.getInstance().getController(Scenes.S_POLL);

            PollSave pollSave = new PollSave(
                    controller.getOptionCount(),
                    controller.getQuestionText(),
                    controller.getOptions()
            );

            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                bw.write(new GsonBuilder().setPrettyPrinting().create().toJson(pollSave));
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            lastFolder = file.getParent();
        }
    }

    public static void saveQuestions() {
        FileChooser fileChooser = getFileChooser();
        File file = fileChooser.showSaveDialog(questionsStage);
        if (file != null) {

            QuestionsController controller = (QuestionsController) AESceneLoader.getInstance().getController(Scenes.S_QUESTIONS);
            List<QuestionSave.QuestionSaveItem> questions = controller.getQuestions().stream()
                    .map(question -> new QuestionSave.QuestionSaveItem(question.getUsername(), question.getTimestamp(), question.getQuestion(), question.isAnswered())).collect(Collectors.toList());
            QuestionSave questionSave = new QuestionSave(questions);

            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                bw.write(new GsonBuilder().setPrettyPrinting().create().toJson(questionSave));
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            lastFolder = file.getParent();
        }
    }

    public static void loadGame() {

        FileChooser fileChooser = getFileChooser();

        //Show save file dialog
        File file = fileChooser.showOpenDialog(inventoryStage);

        if (file != null) {
            //File has been chosen
            //Compile all information about the inventory
            try {
                GameSave gameSave = new Gson().fromJson(new FileReader(file), GameSave.class);
                InventoryController controller = (InventoryController) AESceneLoader.getInstance().getController(Scenes.S_INVENTORY);
                controller.hardReset();
                controller.load(gameSave);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            lastFolder = file.getParent();
        }
    }

    public static void loadPoll() {

        FileChooser fileChooser = getFileChooser();

        //Show save file dialog
        File file = fileChooser.showOpenDialog(pollStage);

        if (file != null) {
            //File has been chosen
            //Compile all information about the inventory
            try {
                PollSave pollSave = new Gson().fromJson(new FileReader(file), PollSave.class);
                PollController controller = (PollController) AESceneLoader.getInstance().getController(Scenes.S_POLL);
                controller.load(pollSave);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            lastFolder = file.getParent();
        }
    }

    public static void loadQuestions() {
        FileChooser fileChooser = getFileChooser();

        //Show save file dialog
        File file = fileChooser.showOpenDialog(questionsStage);

        if (file != null) {
            //File has been chosen
            //Compile all information about the inventory
            try {
                QuestionSave questionSave = new Gson().fromJson(new FileReader(file), QuestionSave.class);
                QuestionsController controller = (QuestionsController) AESceneLoader.getInstance().getController(Scenes.S_QUESTIONS);
                controller.load(questionSave);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            lastFolder = file.getParent();
        }
    }

    private static FileChooser getFileChooser() {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Json files (*.json)", "*.json");
        if (lastFolder != null)
            fileChooser.setInitialDirectory(new File(lastFolder));
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser;
    }

    public static void setInventoryStage(Stage inventoryStage) {
        SaveManager.inventoryStage = inventoryStage;
    }

    public static void setPollStage(Stage pollStage) {
        SaveManager.pollStage = pollStage;
    }

    public static void setQuestionsStage(Stage questionsStage) {
        SaveManager.questionsStage = questionsStage;
    }
}
