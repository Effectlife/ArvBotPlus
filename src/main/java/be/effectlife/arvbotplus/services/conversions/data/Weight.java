package be.effectlife.arvbotplus.services.conversions.data;

import java.util.Arrays;
import java.util.List;

public final class Weight extends CType {//weight (g, kg, lbs, pounds, oz, ounce)
    public static Weight G = new Weight("G", 0.001F);
    public static Weight KG = new Weight("KG", 1F);
    public static Weight POUNDS = new Weight("POUNDS", 2.20462F);
    public static Weight OZ = new Weight("OZ", 35.274F);

    private Weight(String unit, float conversionToBase) {
        this.setUnit(unit);
        this.setConversionToBase(conversionToBase);
    }

    public static List<CType> getAllValues() {
        return Arrays.asList(G, KG, POUNDS, OZ);
    }

    public static String getType() {
        return "Weight"; //TODO: Migrate to MessageKey
    }
}
