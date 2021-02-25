package be.effectlife.arvbotplus.conversions.data;

public enum Weight {//weight (g, kg, lbs, pounds, oz, ounce)
    G(0.001F), KG(1F), LBS(2.20462F),
    POUNDS(2.20462F), OZ(35.274F), OUNCE(35.274F);
    private final float conversionToBase;

    Weight(float conversionToBase) {
        this.conversionToBase = conversionToBase;
    }

    public float getConversionToBase() {
        return conversionToBase;
    }
}
