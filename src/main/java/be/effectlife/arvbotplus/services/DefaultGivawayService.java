package be.effectlife.arvbotplus.services;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class DefaultGivawayService implements GivawayService {

    private String currentKeyword;
    private Set<String> enteredNames = new HashSet<>();
    private Random random = new Random();

    /**
     * Tries to enter a name into the set of names
     *
     * @param name name to enter
     * @return true when a name is added, false when a name already exists in the set
     */
    @Override
    public boolean enterName(String name) {
        return enteredNames.add(name);
    }

    @Override
    public boolean lastCall() {
        return false;
    }

    @Override
    public String drawWinner() {
        return enteredNames.toArray()[random.nextInt(enteredNames.size())].toString();
    }

    @Override
    public boolean setup(String keyword) {
        currentKeyword = keyword;
        return false;
    }

    @Override
    public String getKeyword() {
        return currentKeyword;
    }

    @Override
    public String getWinPercentage() {
        if (enteredNames.isEmpty()) return "N/A";
        return "" + 1.0 / enteredNames.size();
    }
}
