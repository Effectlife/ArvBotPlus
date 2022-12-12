package be.effectlife.arvbotplus.models.dice;

public enum Operator {
    ADD(" + "), SUBTRACT(" - "), MULTIPLY(" * "), DIVIDE(" / "), INVALID("!");

    private final String sign;

    Operator(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return sign;
    }
}
