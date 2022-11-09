package be.effectlife.arvbotplus.utilities;

import javafx.scene.paint.Color;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ColorHelper {
    private static final Logger LOG = LoggerFactory.getLogger(ColorHelper.class);
    private static List<String> list;
    private static final String COLOR_PREFIX = "-color-";

    private static String cssTemplate;

    private ColorHelper() {
    }


    public static void init() {
        String cssFileName = StringUtils.isBlank(cssTemplate) ? "css/arvbotplus-dark.css" : "css/arvbotplus-" + cssTemplate + ".css";
        try (BufferedReader br = new BufferedReader(new FileReader(cssFileName))) {
            list =
                    br.lines()
                            .map(String::trim)
                            .filter(s -> s.startsWith(COLOR_PREFIX))
                            .map(s -> s.substring(0, s.indexOf(';')))
                            .collect(Collectors.toList());
        } catch (IOException e) {
            LOG.error("Couldn't load css file at " + cssFileName);
        }
    }

    private static Color getColorForName(String colorname) {
        for (String s : list) {
            if (s.startsWith(COLOR_PREFIX + colorname.trim() + ":")) {
                String data = s.substring(s.indexOf(':') + 2);
                if (data.startsWith(COLOR_PREFIX)) {
                    return getColorForName(data.replace(COLOR_PREFIX, ""));
                }
                //Found hex data
                String hex = data.replace(COLOR_PREFIX, "");
                return Color.web(hex);
            }
        }
        return null;
    }

    public static String retrieveColor(ColorType color) {
        Color color1 = Color.PURPLE;
        switch (color) {
            case CRIT:
                color1 = ColorHelper.getColorForName("crit");
                break;
            case SUCCESS:
                color1 = ColorHelper.getColorForName("success");
                break;
            case BACKGROUND:
                color1 = ColorHelper.getColorForName("background-pane");
                break;
            case TEXT:
                color1 = ColorHelper.getColorForName("text-main");
                break;
            case HIGHLIGHT:
                color1 = ColorHelper.getColorForName("bottombutton");
                break;
        }
        return color1 != null ? color1.toString().replace("0x", "#") : "#000000";
    }

    public static void setCssTemplate(String t) {
        cssTemplate = t;
    }
}
