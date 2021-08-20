package be.effectlife.testhelpers;

import be.effectlife.javafxextensions.loading.Scenes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestScenes extends Scenes {
    private static final Logger LOG = LoggerFactory.getLogger(TestScenes.class);
    public static final TestScenes TESTNOTFOUND = new TestScenes("file", "title", 100);

    public TestScenes(String file) {
        super(file);
    }

    public TestScenes(String file, String title) {
        super(file, title);
    }

    public TestScenes(String file, String title, int minWidth) {
        super(file, title, minWidth);
    }
}
