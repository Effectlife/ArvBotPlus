package be.effectlife.arvbotplus.saves.models;


import java.util.List;

public class PollOption {
    private String optionText;
    private int votes;
    private List<String> voters;

    public PollOption(String optionName, int counter, List<String> voters) {
        this.optionText = optionName;
        this.votes = counter;
        this.voters = voters;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public List<String> getVoters() {
        return voters;
    }

    public void setVoters(List<String> voters) {
        this.voters = voters;
    }
}
