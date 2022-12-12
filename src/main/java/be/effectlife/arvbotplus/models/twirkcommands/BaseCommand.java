package be.effectlife.arvbotplus.models.twirkcommands;

import com.gikk.twirk.Twirk;
import com.gikk.twirk.commands.CommandExampleBase;
import com.gikk.twirk.enums.USER_TYPE;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;

public abstract class BaseCommand extends CommandExampleBase {
    protected final Twirk twirk;
    protected final boolean disable;

    protected BaseCommand(CommandType type, Twirk twirk, boolean disable) {
        super(type);
        this.twirk = twirk;
        this.disable = disable;
    }

    public BaseCommand(CommandType type) {
        super(type);
        this.twirk = null;
        this.disable = true;
    }

    protected USER_TYPE getMinUserPrevilidge() {
        return USER_TYPE.DEFAULT;
    }

    @Override
    protected void performCommand(String command, TwitchUser sender, TwitchMessage message) {
        String content = message.getContent().trim();
        if (!content.startsWith(getCommandWords())) return;
        handleCommand(content, sender, message);
    }

    abstract void handleCommand(String content, TwitchUser sender, TwitchMessage message);
}
