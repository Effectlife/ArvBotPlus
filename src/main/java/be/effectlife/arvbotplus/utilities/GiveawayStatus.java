package be.effectlife.arvbotplus.utilities;

public enum GiveawayStatus {
    NOT_RUNNING("Not Running"), RUNNING("Running"), LAST_CALL("Last Call!");
    private final String display;

    GiveawayStatus(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }
}
