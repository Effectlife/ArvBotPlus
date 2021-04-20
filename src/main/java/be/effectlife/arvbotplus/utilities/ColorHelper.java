package be.effectlife.arvbotplus.utilities;

import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ColorHelper {
    private static List<String> list;
    static {
        try {

            List<String> doc;
            try (InputStream resource = ColorHelper.class.getResourceAsStream("/css/bootstrap3-dark.css")) {
                 doc =
                        new BufferedReader(new InputStreamReader(resource,
                                StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
            }
            list = doc
                    .stream()
                    .map(String::trim)
                    .filter(s -> s.startsWith("-color-"))
                    .map(s -> s.substring(0, s.indexOf(";")))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Color getColorForName(String colorname) {
        for (String s : list) {
            if (s.startsWith("-color-" + colorname.trim() + ":")) {
                String data = s.substring(s.indexOf(':') + 2);
                if (data.startsWith("-color-")) {
                    return getColorForName(data.replace("-color-", ""));
                }
                //Found hex data
                String hex = data.replace("-color-", "");
                return Color.web(hex);
            }
        }
        return null;
    }
}
