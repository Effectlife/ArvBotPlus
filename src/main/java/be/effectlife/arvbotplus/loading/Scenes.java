package be.effectlife.arvbotplus.loading;

import org.apache.commons.lang3.StringUtils;

public enum Scenes {

    S_POLL("s_poll", "Polls", 350),
    S_INVENTORY("s_inventory", "Inventory", 500),
    W_POLL("widgets/w_poll"),
    W_QUICKPOLL("widgets/w_quickPoll"),
    W_SKILL("widgets/w_skill"),
    S_LOADING("s_loading", "Loading");

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
