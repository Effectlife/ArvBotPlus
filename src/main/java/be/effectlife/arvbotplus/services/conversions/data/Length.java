package be.effectlife.arvbotplus.services.conversions.data;

import be.effectlife.arvbotplus.loading.MessageKey;
import be.effectlife.arvbotplus.loading.MessageProperties;

import java.util.Arrays;
import java.util.List;

public final class Length extends CType {
    public static final Length KM = new Length("KM", "km", 0.001f);
    public static final Length M = new Length("M", "m", 1f);
    public static final Length CM = new Length("CM", "cm", 100f);
    public static final Length FT = new Length("FT", "ft", 3.28084f);
    public static final Length YARD = new Length("YARD", "yards", 1.09361f);
    public static final Length MILE = new Length("MILE", "miles", 0.000621371f);

    private Length(String unit, String displayName, float conversionToBase) {
        this.setUnit(unit);
        this.setDisplayName(displayName);
        this.setConversionToBase(conversionToBase);
    }

    public static List<CType> getAllValues() {
        return Arrays.asList(KM, M, CM, FT, YARD, MILE);
    }

    public static String getType() {
        return MessageProperties.getString(MessageKey.CONVERSIONS_TYPE_LENGTH);
    }
}
