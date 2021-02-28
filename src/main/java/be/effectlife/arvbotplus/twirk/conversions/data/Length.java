package be.effectlife.arvbotplus.twirk.conversions.data;

public enum Length {
    KM(0.001f), M(1f), CM(100f),
    FT(3.28084f), YARD(1.09361f), MILE(0.000621371f);
    private final float conversionToBase;

    Length(float conversionToBase) {
        this.conversionToBase = conversionToBase;
    }

    public float getConversionToBase() {
        return conversionToBase;
    }
}
