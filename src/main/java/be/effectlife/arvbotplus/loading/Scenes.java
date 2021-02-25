package be.effectlife.arvbotplus.loading;

import org.apache.commons.lang3.StringUtils;

public enum Scenes {

    S_POLL("s_poll", "Polls"),
    S_INVENTORY("s_inventory", "Inventory"),
    W_POLL("widgets/w_poll"),
    W_QUICKPOLL("widgets/w_quickPoll");

    private final String title;
    private final String file;

    Scenes(String file) {
        this(file, "");
    }

    Scenes(String file, String title) {
        this.file = file;
        this.title = title;
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
}
