package be.effectlife.arvbotplus.services;

import be.effectlife.arvbotplus.models.dice.Die;
import be.effectlife.arvbotplus.models.dice.DieRoll;
import be.effectlife.arvbotplus.models.dice.Mod;
import be.effectlife.arvbotplus.models.dice.Operator;
import be.effectlife.arvbotplus.utilities.InvalidDieOperatorException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.stream.Collectors;

import static be.effectlife.arvbotplus.models.dice.Operator.*;

public class DieService {
    private static final Logger LOG = LoggerFactory.getLogger(DieService.class);

    private DieService() {
    }

    public static DieRoll parseRollInternal(String[] splitOnNormalOp, String roll) {
        DieRoll diceRoll = new DieRoll();
        diceRoll.setRawExpression(roll);
        for (String s : Arrays.stream(splitOnNormalOp).filter(StringUtils::isNotBlank).collect(Collectors.toList())) {
            if (s.contains("d") && !s.contains("(")) {
                //Is Die
                processDie(roll, diceRoll, s);
            } else if (StringUtils.isNumeric(s) || StringUtils.isNumeric(s.substring(1))) {
                processMod(diceRoll, s);
            } else if (s.contains("(") && s.contains(")")) {
                processSubRoll(diceRoll, s);
            } else {
                String exceptionMessage = String.format("Unknown token (%s) found in expression. This expression cannot be evaluated. (%s)", s, diceRoll.getRawExpression());
                LOG.warn(exceptionMessage);
                throw new IllegalArgumentException(exceptionMessage);
            }
        }
        return diceRoll;
    }

    private static void processSubRoll(DieRoll diceRoll, String s) {
        String substring = s.substring(s.indexOf('(') + 1, s.length() - 1);
        DieRoll subRoll = DieRoll.parseRoll(substring);
        if (s.charAt(0) != '(') {
            subRoll.setOperator(getOperator(s.charAt(0)));
        } else {
            subRoll.setOperator(ADD);
        }
        diceRoll.add(subRoll);
    }

    private static void processMod(DieRoll diceRoll, String s) {
        char firstChar = s.charAt(0);
        if (!Character.isDigit(firstChar)) {
            diceRoll.add(new Mod(getOperator(firstChar), Integer.parseInt(s.substring(1))));
        } else {
            diceRoll.add(new Mod(ADD, Integer.parseInt(s)));
        }
    }

    private static void processDie(String roll, DieRoll diceRoll, String s) {
        final String[] dieInfo = s.split("d");
        final char firstChar;
        if (dieInfo[0].isEmpty()) firstChar = '1';
        else firstChar = dieInfo[0].charAt(0);
        if (!Character.isDigit(firstChar)) {
            //Has a designated operator
            Operator operator = getOperator(firstChar);
            if (operator == INVALID) {
                throw new InvalidDieOperatorException(String.format("Invalid Operator added to roll '%s' at '%s'; Operator: '%s'", roll, s, firstChar));
            }
            if (dieInfo[0].length() == 1) {
                diceRoll.add(new Die(operator, 1, Integer.parseInt(dieInfo[1])));
            } else {
                diceRoll.add(new Die(operator, Integer.parseInt(dieInfo[0].substring(1)), Integer.parseInt(dieInfo[1])));
            }
        } else {
            diceRoll.add(new Die(ADD, Integer.parseInt(StringUtils.isNotBlank(dieInfo[0]) ? dieInfo[0] : "1"), Integer.parseInt(dieInfo[1])));
        }
    }

    private static Operator getOperator(char firstChar) {
        Operator operator = INVALID;
        if (firstChar == '+') operator = ADD;
        else if (firstChar == '-') operator = SUBTRACT;
        else if (firstChar == '*') operator = MULTIPLY;
        else if (firstChar == '/') operator = DIVIDE;
        return operator;
    }
}
