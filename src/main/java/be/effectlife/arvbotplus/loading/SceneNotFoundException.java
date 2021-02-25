package be.effectlife.arvbotplus.loading;

import java.io.IOException;

public class SceneNotFoundException extends IOException {
    public SceneNotFoundException() {
        super();
    }

    public SceneNotFoundException(String s) {
        super(s);
    }

    public SceneNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SceneNotFoundException(Throwable cause) {
        super(cause);
    }
}
