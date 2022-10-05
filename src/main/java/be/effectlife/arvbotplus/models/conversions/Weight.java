package be.effectlife.arvbotplus.models.conversions;

import be.effectlife.arvbotplus.loading.MessageKey;
import be.effectlife.arvbotplus.loading.MessageProperties;

import java.util.Arrays;
import java.util.List;

public final class Weight extends CType {//weight (g, kg, lbs, pounds, oz, ounce)
    public static final Weight G = new Weight("G", "g", 0.001F);
    public static final Weight KG = new Weight("KG", "kg", 1F);
    public static final Weight POUNDS = new Weight("POUNDS", "pounds", 2.20462F);
    public static final Weight OZ = new Weight("OZ", "ounces", 35.274F);

    private Weight(String unit, String displayName, float conversionToBase) {
        this.setUnit(unit);
        setDisplayName(displayName);
        this.setConversionToBase(conversionToBase);
    }

    public static List<CType> getAllValues() {
        return Arrays.asList(G, KG, POUNDS, OZ);
    }

    public static String getType() {
        return MessageProperties.getString(MessageKey.CONVERSIONS_TYPE_WEIGHT);
    }
}
