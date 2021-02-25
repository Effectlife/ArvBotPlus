package be.effectlife.arvbotplus.conversions.data;

public enum Volume {
    LITER(1f), GALLON(0.264172f), QUART(1.05669f),
    M3(0.001f), CI(61.0237f), CC(1000f);
    private final float conversionToBase;

    Volume(float conversionToBase) {
        this.conversionToBase = conversionToBase;
    }

    public float getConversionToBase() {
        return conversionToBase;
    }
}
