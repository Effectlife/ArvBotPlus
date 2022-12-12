package be.effectlife.arvbotplus.models.dice;

public class Mod implements DieBase {
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
