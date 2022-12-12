package be.effectlife.arvbotplus.utilities;
public class DiceUtility {
    private DiceUtility() {
    }

    public static String addSuccess(int diceRoll) {
        return "<span class=\"color-success\">" + diceRoll + "</span>";
    }
    @SuppressWarnings("squid:S3400")
    public static String addFail() {
        return "<span class=\"color-crit\">1</span>";
    }

    public static String addNormal(int diceRoll) {
        return "<span>" + diceRoll + "</span>";
    }
}
