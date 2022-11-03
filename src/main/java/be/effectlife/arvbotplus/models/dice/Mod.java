package be.effectlife.arvbotplus.models.dice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mod implements DieBase {
    private static final Logger LOG = LoggerFactory.getLogger(Mod.class);
    private Operator operator;
    private int value;

    public Mod(Operator operator, int value) {
        this.operator = operator;
        this.value = value;
    }

    public Mod() {
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public double getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Mod{" +
                "operator=" + operator +
                ", value=" + value +
                '}';
    }
}
