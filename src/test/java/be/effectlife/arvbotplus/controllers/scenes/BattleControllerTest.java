package be.effectlife.arvbotplus.controllers.scenes;


import be.effectlife.AbstractJavaFxTest;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BattleControllerTest extends AbstractJavaFxTest {
    @InjectMocks
private BattleController battleController;
    @Mock
    private GridPane gpBase;

    @Mock
    private Button btnAdd;

    @Mock
    private Button btnRemove;

    @Mock
    private Button btnClear;
    @Mock
    private Text textVal1;

    @Mock
    private Text textVal2;
    @Mock
    private VBox vboxEnemies;

    @Test
    public void btnAddClick_CreatesNewWidget(){
        battleController.btnAddClick(null);
    }
}