package be.effectlife.arvbotplus.utilities;

import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

import static java.lang.String.format;

public class BasicUtilities {
    private static final Logger LOG = LoggerFactory.getLogger(BasicUtilities.class);

    private BasicUtilities() {
    }

    public static Properties loadProperties(String propFileName, String defaults) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(propFileName);
        } catch (FileNotFoundException e) {
            LOG.error("Cannot find properties file {}; Generating default file and exiting program. Please check twitch configuration", propFileName);
            try {
                writePropertiesFile(propFileName, defaults);
            } catch (IOException ioException) {
                LOG.error("Exception happened while writing default properties file", ioException);
            }
            Platform.runLater(() -> {
                SimplePopup.showPopupError(format("Cannot find properties file %s; Generating default file and exiting program. Please check twitch configuration", propFileName));
                System.exit(1);
            });
        }
        properties.load(inputStream);
        return properties;
    }

    private static void writePropertiesFile(String propFileName, String content) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(propFileName))) {
            bw.write(content);
            bw.flush();
        }
    }
}
