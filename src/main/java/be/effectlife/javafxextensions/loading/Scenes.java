package be.effectlife.javafxextensions.loading;

import org.apache.commons.lang3.StringUtils;

public abstract class Scenes {
    private final String title;
    private final String file;
    private final int minWidth;

    public Scenes(String file) {
        this(file, "");
    }

    public Scenes(String file, String title) {
        this(file, title, 0);
    }

    public Scenes(String file, String title, int minWidth) {
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
        return file;
    }

    public String getFile() {
        return file;
    }

    public int getMinWidth() {
        return minWidth;
    }
}
