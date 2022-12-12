package be.effectlife.arvbotplus.models.dice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Die implements DieBase {
    private static final Logger LOG = LoggerFactory.getLogger(Die.class);
    private Operator operator;
    private int dieCount;
    private int dieSize;

    public Die(int dieCount, int dieSize) {
        this.dieCount = dieCount;
        this.dieSize = dieSize;
    }

    public Die(Operator operator, int dieCount, int dieSize) {
        this.operator = operator;
        this.dieCount = dieCount;
        this.dieSize = dieSize;
    }

    public Die() {

    }

    public int getDieCount() {
        return dieCount;
    }

    public void setDieCount(int dieCount) {
        this.dieCount = dieCount;
    }

    public int getDieSize() {
        return dieSize;
    }

    public void setDieSize(int dieSize) {
        this.dieSize = dieSize;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return operator.toString() + dieCount + "d" + dieSize;
    }

    public double getValue() {
        LOG.warn("Should not get value of a raw die ({}). This should already have been converted to a modifier", this);
        return 0;
    }
}
