package be.effectlife.arvbotplus.models.dice;

import be.effectlife.arvbotplus.utilities.DiceUtility;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static be.effectlife.arvbotplus.models.dice.Operator.*;

public class DieRoll implements DieBase {
    static Logger LOG = LoggerFactory.getLogger(DieRoll.class);
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
        return parseRollInternal(splitOnNormalOp, roll);

    }

    private static DieRoll parseRollInternal(String[] splitOnNormalOp, String roll) {
        DieRoll diceRoll = new DieRoll();
        diceRoll.rawExpression = roll;
        for (String s : Arrays.stream(splitOnNormalOp).filter(StringUtils::isNotBlank).collect(Collectors.toList())) {
            if (s.contains("d") && !s.contains("(")) {
                //Is Die
                final String[] dieInfo = s.split("d");
                final char firstChar;
                if (dieInfo[0].isEmpty()) firstChar = '1';
                else firstChar = dieInfo[0].charAt(0);
                if (!Character.isDigit(firstChar)) {
                    //Has a designated operator
                    Operator operator = getOperator(firstChar);
                    if (operator == INVALID) {
                        throw new RuntimeException(String.format("Invalid Operator added to roll '%s' at '%s'; Operator: '%s'", roll, s, firstChar));
                    }
                    if (dieInfo[0].length() == 1) {
                        diceRoll.add(new Die(operator, 1, Integer.parseInt(dieInfo[1])));
                    } else {
                        diceRoll.add(new Die(operator, Integer.parseInt(dieInfo[0].substring(1)), Integer.parseInt(dieInfo[1])));
                    }
                } else {
                    diceRoll.add(new Die(ADD, Integer.parseInt(StringUtils.isNotBlank(dieInfo[0]) ? dieInfo[0] : "1"), Integer.parseInt(dieInfo[1])));
                }
            } else if (StringUtils.isNumeric(s) || StringUtils.isNumeric(s.substring(1))
//                    || StringUtils.equals(s.charAt(0) + "", "(") && StringUtils.isNumeric(s.charAt(1) + "")
            ) {
                //"5", "+5", "-5"
                char firstChar = s.charAt(0);
                if (!Character.isDigit(firstChar)) {
                    diceRoll.add(new Mod(getOperator(firstChar), Integer.parseInt(s.substring(1))));
                } else {
                    diceRoll.add(new Mod(ADD, Integer.parseInt(s)));
                }
            } else if (s.contains("(") && s.contains(")")) {
                //"+(3d8+3)", "-(3d8+3)", "(3d8+3)"
                String substring = s.substring(s.indexOf("(") + 1, s.length() - 1);
                DieRoll subRoll = DieRoll.parseRoll(substring);
                if (s.charAt(0) != '(') {
                    subRoll.setOperator(getOperator(s.charAt(0)));
                } else {
                    subRoll.setOperator(ADD);
                }
                diceRoll.add(subRoll);
            } else {
                LOG.warn("HUH? What's this: " + s);
                throw new IllegalArgumentException("Unknown token ("+s+") found in expression. This expression cannot be evaluated. ("+ diceRoll.rawExpression+")");
            }
        }
        return diceRoll;
    }

    private static Operator getOperator(char firstChar) {
        Operator operator = INVALID;
        if (firstChar == '+') operator = ADD;
        else if (firstChar == '-') operator = SUBTRACT;
        else if (firstChar == '*') operator = MULTIPLY;
        else if (firstChar == '/') operator = DIVIDE;
        return operator;
    }

    public static DieRoll createSimpleResult(double result) {
        DieRoll dieRoll = new DieRoll();
        dieRoll.cachedResult = result;
        return dieRoll;
    }

    private void add(DieBase item) {
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
        if (rolledExpression == null) doCalculation();
        return rolledExpression;
    }

    public String getExpressionHtml() {
        if (rolledExpressionHtml == null) doCalculation();
        return rolledExpressionHtml;
    }

    public void doCalculation() {

        StringBuilder sbRolledExpression = new StringBuilder();
        StringBuilder sbRolledExpressionHtml = new StringBuilder();

        for (int j = 0; j < elements.size(); j++) {
            DieBase element = elements.get(j);
            if (element instanceof Die) {
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

            } else if (element instanceof Mod) {
                if (j != 0 || element.getOperator() != ADD) {
                    appendOperator(sbRolledExpression, element);
                    appendOperator(sbRolledExpressionHtml, element);
                }
                sbRolledExpression.append(element.getValue());
                sbRolledExpressionHtml.append(element.getValue());
            } else if (element instanceof DieRoll) {
                if (j != 0 || element.getOperator() != ADD) {
                    appendOperator(sbRolledExpression, element);
                    appendOperator(sbRolledExpressionHtml, element);
                }
                sbRolledExpression.append("(").append(((DieRoll) element).getExpression()).append(")");
                sbRolledExpressionHtml.append("(").append(((DieRoll) element).getExpression()).append(")");
            }
        }
        rolledExpression = sbRolledExpression.toString();
        rolledExpressionHtml = sbRolledExpressionHtml.toString();
    }


    public String getRawExpression() {
        return rawExpression;
    }

    private static void appendOperator(StringBuilder sb, DieBase element) {
        sb.append(element.getOperator());
    }
}
