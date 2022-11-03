package be.effectlife.arvbotplus.models.dice;

public class Dice {
    public static void main(String[] args) {
        String[] rolls = {
                "6d6",


        };
        //for (int i = 0; i < 5; i++) {
            //System.out.println("-------"+i+"-------");
            for (String roll : rolls) {
                final DieRoll dieRoll = DieRoll.parseRoll(roll);
                System.out.println(dieRoll.getRawExpression());
                System.out.println(dieRoll.getExpression());
                System.out.println(dieRoll.getExpressionHtml());
                System.out.println(dieRoll.getValue());
            }
        //}
    }
}
