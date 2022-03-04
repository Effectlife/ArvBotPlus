package be.effectlife.arvbotplus.saves.models;

import java.util.List;

public class GameSave {
    private String name;
    private String itemsArtifacts;
    private String cluesNotes;
    private List<Skill> skills;

    public GameSave() {
    }

    public GameSave(String name, String itemsArtifacts, String cluesNotes, List<Skill> skills) {
        this.name = name;
        this.itemsArtifacts = itemsArtifacts;
        this.cluesNotes = cluesNotes;
        this.skills = skills;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemsArtifacts() {
        return itemsArtifacts;
    }

    public void setItemsArtifacts(String itemsArtifacts) {
        this.itemsArtifacts = itemsArtifacts;
    }

    public String getCluesNotes() {
        return cluesNotes;
    }

    public void setCluesNotes(String cluesNotes) {
        this.cluesNotes = cluesNotes;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }
}
