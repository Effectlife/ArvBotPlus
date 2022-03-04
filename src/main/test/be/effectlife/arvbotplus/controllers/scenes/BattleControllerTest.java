package be.effectlife.arvbotplus.controllers.scenes;

import be.effectlife.arvbotplus.JfxTestBase;
import com.sun.javafx.application.PlatformImpl;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javax.swing.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(MockitoJUnitRunner.class)
public class BattleControllerTest extends JfxTestBase {

    @InjectMocks
    private BattleController battleController;

    @Spy
    private GridPane gpBase;

    @Spy
    private Button btnAdd;

    @Spy
    private Button btnRemove;

    @Spy
    private Button btnClear;

    @Spy
    private Text textVal1;

    @Spy
    private Text textVal2;

    @Spy
    private VBox vboxEnemies;

    @Test
    public void testDoInit(){
        battleController.doInit();
    }

}