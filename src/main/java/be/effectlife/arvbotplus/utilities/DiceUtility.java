package be.effectlife.arvbotplus.utilities;

import be.effectlife.arvbotplus.models.dice.DieRoll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiceUtility {
    private static final Logger LOG = LoggerFactory.getLogger(DiceUtility.class);

    public static String addSuccess(int diceRoll) {
        return "<span class=\"color-success\">" + diceRoll + "</span>";
    }

    public static String addFail() {
        return "<span class=\"color-crit\">1</span>";
    }

    public static String addNormal(int diceRoll) {
        return "<span>" + diceRoll + "</span>";
    }
}
