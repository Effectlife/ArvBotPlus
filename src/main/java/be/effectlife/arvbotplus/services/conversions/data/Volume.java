package be.effectlife.arvbotplus.services.conversions.data;

import java.util.Arrays;
import java.util.List;

public final class Volume extends CType {
    public static Volume LITER = new Volume("LITER", 1f);
    public static Volume GALLON = new Volume("GALLON", 0.264172f);
    public static Volume QUART = new Volume("QUART", 1.05669f);
    public static Volume M3 = new Volume("M3", 0.001f);
    public static Volume CI = new Volume("CI", 61.0237f);
    public static Volume CC = new Volume("CC", 1000f);

    private Volume(String unit, float conversionToBase) {
        this.setUnit(unit);
        this.setConversionToBase(conversionToBase);
    }

    public static List<CType> getAllValues() {
        return Arrays.asList(LITER, GALLON, QUART, M3, CI, CC);
    }

    public static String getType() {
        return "Volume"; //TODO: Migrate to MessageKey
    }
}
