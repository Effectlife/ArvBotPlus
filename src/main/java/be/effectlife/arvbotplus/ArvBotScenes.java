package be.effectlife.arvbotplus;

import be.effectlife.javafxextensions.loading.Scenes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArvBotScenes extends Scenes {
    private static final Logger LOG = LoggerFactory.getLogger(ArvBotScenes.class);

    public static final Scenes S_POLL = new ArvBotScenes("s_poll", "Polls", 350);
    public static final Scenes S_INVENTORY = new ArvBotScenes("s_inventory", "Inventory", 510);
    public static final Scenes S_DICE = new ArvBotScenes("s_dice", "Dice Roller", 250);
    public static final Scenes S_CONV = new ArvBotScenes("s_conv", "Conversions", 200);
    public static final Scenes S_BATTLE = new ArvBotScenes("s_battle", "Battle Manager", 380);
    public static final Scenes W_POLL = new ArvBotScenes("widgets/w_poll");
    public static final Scenes W_QUICKPOLL = new ArvBotScenes("widgets/w_quickPoll");
    public static final Scenes W_SKILL = new ArvBotScenes("widgets/w_skill");
    public static final Scenes W_DICERESULT = new ArvBotScenes("widgets/w_diceResult");
    public static final Scenes W_ENEMY = new ArvBotScenes("widgets/w_enemy");
    public static final Scenes S_QUESTIONS = new ArvBotScenes("s_questions", "Questions", 600);
    public static final Scenes W_QUESTION = new ArvBotScenes("widgets/w_question");

    ArvBotScenes(String file) {
        this(file, "");
    }

    ArvBotScenes(String file, String title) {
        this(file, title, 0);
    }

    ArvBotScenes(String file, String title, int minWidth) {
        super(file, title, minWidth);
    }

}
