package be.effectlife.arvbotplus.models.dice;

public class Die implements DieBase {
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
        return "Die{" +
                "operator=" + operator +
                ", dieCount=" + dieCount +
                ", dieSize=" + dieSize +
                '}';
    }

    public double getValue() {
        throw new RuntimeException("Should not get value of a raw die. This should already have been converted to a modifier");
    }
}
