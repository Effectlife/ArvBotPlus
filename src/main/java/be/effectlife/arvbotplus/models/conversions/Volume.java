package be.effectlife.arvbotplus.models.conversions;

import be.effectlife.arvbotplus.loading.MessageKey;
import be.effectlife.arvbotplus.loading.MessageProperties;

import java.util.Arrays;
import java.util.List;

public final class Volume extends CType {
    public static final Volume LITER = new Volume("LITER", "liters", 1f);
    public static final Volume GALLON = new Volume("GALLON", "gallons", 0.264172f);
    public static final Volume QUART = new Volume("QUART", "quarts", 1.05669f);
    public static final Volume M3 = new Volume("M3", "m³", 0.001f);
    public static final Volume CI = new Volume("CI", "ci", 61.0237f);
    public static final Volume CC = new Volume("CC", "cc", 1000f);

    private Volume(String unit, String displayName, float conversionToBase) {
        this.setUnit(unit);
        this.setDisplayName(displayName);
        this.setConversionToBase(conversionToBase);
    }

    public static List<CType> getAllValues() {
        return Arrays.asList(LITER, GALLON, QUART, M3, CI, CC);
    }

    public static String getType() {
        return MessageProperties.getString(MessageKey.CONVERSIONS_TYPE_VOLUME);
    }
}
