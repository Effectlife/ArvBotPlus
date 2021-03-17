package be.effectlife.arvbotplus.saves.models;

import be.effectlife.arvbotplus.utilities.SkillType;

public class Skill {
    private String name;
    private SkillType skillType;
    private int value, maxValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SkillType getSkillType() {
        return skillType;
    }

    public void setSkillType(SkillType skillType) {
        this.skillType = skillType;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public Skill() {
    }

    public Skill(String name, SkillType skillType, int value, int maxValue) {
        this.name = name;
        this.skillType = skillType;
        this.value = value;
        this.maxValue = maxValue;
    }
}
