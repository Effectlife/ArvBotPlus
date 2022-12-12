package be.effectlife.arvbotplus.models.dice;

import be.effectlife.arvbotplus.services.DieService;
import be.effectlife.arvbotplus.utilities.DiceUtility;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static be.effectlife.arvbotplus.models.dice.Operator.ADD;

public class DieRoll implements DieBase {
    private static final Random r = new Random();
    private final List<DieBase> elements = new ArrayList<>();
    private Operator operator = ADD;

    private String rawExpression;
    private String rolledExpression;
    private String rolledExpressionHtml;
    Double cachedResult;

    public static DieRoll parseRoll(String roll) {
        // (?<=(\()).*(?=(\))) -- Selects Roll without the brackets
        roll = roll.replaceAll("\\s", "");
        final String[] splitOnBracketOpen = roll.split("(?=[+\\-\\\\*/]\\()");
        String[] splitOnBracketClose = advancedSplit(splitOnBracketOpen);
        String[] splitOnNormalOp = splitIgnoreBrackets(splitOnBracketClose);
        return DieService.parseRollInternal(splitOnNormalOp, roll);

    }

    public static DieRoll createSimpleResult(double result) {
        DieRoll dieRoll = new DieRoll();
        dieRoll.cachedResult = result;
        return dieRoll;
    }

    public void add(DieBase item) {
        elements.add(item);
    }

    private static String[] advancedSplit(String[] strings) {
        return Arrays.stream(strings).map(s -> s.split("(?<=\\))")).flatMap(Arrays::stream).toArray(String[]::new);
    }

    private static String[] splitIgnoreBrackets(String[] strings) {
        List<String> list = new ArrayList<>();
        for (String string : strings) {
            if (string.contains("(")) list.add(string);
            else {
                list.addAll(Arrays.asList(string.split("(?=[+\\-*/])")));
            }
        }
        return list.toArray(new String[0]);
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public double getValue() {
        if (cachedResult == null) {
            cachedResult = ((int) ((new ExpressionBuilder(getExpression()).build().evaluate()) * 100)) / 100.0;
        }
        return cachedResult; // Multiply by 100, cast to int, divide by 100.0 to round result to two digits
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }


    @Override
    public String toString() {
        return "DieRoll{" + "elements=" + elements + ", operator=" + operator + '}';
    }

    public String getExpression() {
        if (rolledExpression == null) convertExpressionToStrings();
        return rolledExpression;
    }

    public String getExpressionHtml() {
        if (rolledExpressionHtml == null) convertExpressionToStrings();
        return rolledExpressionHtml;
    }

    public void convertExpressionToStrings() {

        StringBuilder sbRolledExpression = new StringBuilder();
        StringBuilder sbRolledExpressionHtml = new StringBuilder();

        for (int j = 0; j < elements.size(); j++) {
            DieBase element = elements.get(j);
            if (element instanceof Die) {
                convertDie(sbRolledExpression, sbRolledExpressionHtml, j, element);

            } else if (element instanceof Mod) {
                convertMod(sbRolledExpression, sbRolledExpressionHtml, j, element);
            } else if (element instanceof DieRoll) {
                convertDieRoll(sbRolledExpression, sbRolledExpressionHtml, j, element);
            }
        }
        rolledExpression = sbRolledExpression.toString();
        rolledExpressionHtml = sbRolledExpressionHtml.toString();
    }

    private static void convertDieRoll(StringBuilder sbRolledExpression, StringBuilder sbRolledExpressionHtml, int j, DieBase element) {
        if (j != 0 || element.getOperator() != ADD) {
            appendOperator(sbRolledExpression, element);
            appendOperator(sbRolledExpressionHtml, element);
        }
        sbRolledExpression.append("(").append(((DieRoll) element).getExpression()).append(")");
        sbRolledExpressionHtml.append("(").append(((DieRoll) element).getExpression()).append(")");
    }

    private static void convertMod(StringBuilder sbRolledExpression, StringBuilder sbRolledExpressionHtml, int j, DieBase element) {
        if (j != 0 || element.getOperator() != ADD) {
            appendOperator(sbRolledExpression, element);
            appendOperator(sbRolledExpressionHtml, element);
        }
        sbRolledExpression.append(element.getValue());
        sbRolledExpressionHtml.append(element.getValue());
    }

    private static void convertDie(StringBuilder sbRolledExpression, StringBuilder sbRolledExpressionHtml, int j, DieBase element) {
        if (j != 0 || element.getOperator() != ADD) {
            appendOperator(sbRolledExpression, element);
            appendOperator(sbRolledExpressionHtml, element);
        }
        if (((Die) element).getDieCount() > 1) {
            sbRolledExpression.append("(");
            sbRolledExpressionHtml.append("(");
        }
        Die die = (Die) element;
        for (int i = 0; i < die.getDieCount(); i++) {
            if (i != 0) {
                sbRolledExpression.append(ADD);
                sbRolledExpressionHtml.append(ADD);
            }
            int dieResult = r.nextInt(die.getDieSize()) + 1;
            if (dieResult == die.getDieSize()) sbRolledExpressionHtml.append(DiceUtility.addSuccess(dieResult));
            else if (dieResult == 1) sbRolledExpressionHtml.append(DiceUtility.addFail());
            else sbRolledExpressionHtml.append(DiceUtility.addNormal(dieResult));
            sbRolledExpression.append(dieResult);
        }
        if (((Die) element).getDieCount() > 1) {
            sbRolledExpression.append(")");
            sbRolledExpressionHtml.append(")");
        }
    }

    public List<DieBase> getElements() {
        return elements;
    }

    public void setRawExpression(String rawExpression) {
        this.rawExpression = rawExpression;
    }

    public String getRolledExpression() {
        return rolledExpression;
    }

    public void setRolledExpression(String rolledExpression) {
        this.rolledExpression = rolledExpression;
    }

    public String getRolledExpressionHtml() {
        return rolledExpressionHtml;
    }

    public void setRolledExpressionHtml(String rolledExpressionHtml) {
        this.rolledExpressionHtml = rolledExpressionHtml;
    }

    public String getRawExpression() {
        return rawExpression;
    }

    private static void appendOperator(StringBuilder sb, DieBase element) {
        sb.append(element.getOperator());
    }
}
