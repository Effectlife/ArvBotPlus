package be.effectlife.arvbotplus.loading;

import org.apache.commons.lang3.StringUtils;

public enum Scenes {

    S_POLL("s_poll", "Polls", 450),
    S_INVENTORY("s_inventory", "Inventory", 510),
    S_DICE("s_dice", "Dice Roller", 250),
    S_CONV("s_conv", "Conversions", 200),
    S_BATTLE("s_battle", "Battle Manager", 380),
    W_POLL("widgets/w_poll"),
    W_QUICKPOLL("widgets/w_quickPoll"),
    W_SKILL("widgets/w_skill"),
    W_DICERESULT("widgets/w_diceResult"),
    W_ENEMY("widgets/w_enemy"),
    S_QUESTIONS("s_questions", "Questions", 600),
    W_QUESTION("widgets/w_question");

    private final String title;
    private final String file;
    private final int minWidth;

    Scenes(String file) {
        this(file, "");
    }

    Scenes(String file, String title) {
        this(file, title, 0);
    }

    Scenes(String file, String title, int minWidth) {
        this.file = file;
        this.title = title;
        this.minWidth = minWidth;
    }

    @Override
    public String toString() {
        return file;
    }

    public String getTitle() {
        if (StringUtils.isNotBlank(title))
            return title;
        return name();
    }

    public String getFile() {
        return file;
    }

    public int getMinWidth() {
        return minWidth;
    }
}
