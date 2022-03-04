package be.effectlife.arvbotplus;

import com.sun.javafx.application.PlatformImpl;
import javafx.embed.swing.JFXPanel;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class JfxTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(JfxTestBase.class);

    @BeforeClass
    public static void initToolkit()
            throws InterruptedException
    {
        final CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            new JFXPanel(); // initializes JavaFX environment
            latch.countDown();
        });

        // That's a pretty reasonable delay... Right?
        if (!latch.await(5L, TimeUnit.SECONDS))
            throw new ExceptionInInitializerError();

        PlatformImpl.startup(()->{});

    }
}
