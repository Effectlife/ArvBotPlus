package be.effectlife.arvbotplus.saves.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PollSave {
    private static final Logger LOG = LoggerFactory.getLogger(PollSave.class);
    int optionCount;
    String question;
    List<PollOption> options;

    public PollSave(int optionCount, String question, List<PollOption> options) {
        this.optionCount = optionCount;
        this.question = question;
        this.options = options;
    }

    public int getOptionCount() {
        return optionCount;
    }

    public void setOptionCount(int optionCount) {
        this.optionCount = optionCount;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<PollOption> getOptions() {
        return options;
    }

    public void setOptions(List<PollOption> options) {
        this.options = options;
    }
}
