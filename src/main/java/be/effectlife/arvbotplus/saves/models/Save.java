package be.effectlife.arvbotplus.saves.models;

import java.util.List;

public class Save {
    private String saveName;
    private String itemsArtifacts;
    private String cluesNotes;
    private List<Skill> skills;

    public String getSaveName() {
        return saveName;
    }

    public void setSaveName(String saveName) {
        this.saveName = saveName;
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

    public Save() {
    }

    public Save(String saveName, String itemsArtifacts, String cluesNotes, List<Skill> skills) {
        this.saveName = saveName;
        this.itemsArtifacts = itemsArtifacts;
        this.cluesNotes = cluesNotes;
        this.skills = skills;
    }
}
