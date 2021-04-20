package be.effectlife.arvbotplus.services.conversions.data;

import java.util.Arrays;
import java.util.List;

public final class Length extends CType {
    public static final Length KM = new Length("KM", 0.001f);
    public static final Length M = new Length("M", 1f);
    public static final Length CM = new Length("CM", 100f);
    public static final Length FT = new Length("FT", 3.28084f);
    public static final Length YARD = new Length("YARD", 1.09361f);
    public static final Length MILE = new Length("MILE", 0.000621371f);

    private Length(String unit, float conversionToBase) {
        this.setUnit(unit);
        this.setConversionToBase(conversionToBase);
    }

    public static List<CType> getAllValues() {
        return Arrays.asList(KM, M, CM, FT, YARD, MILE);
    }

    public static String getType() {
        return "Length"; //TODO: Migrate to MessageKey
    }
}
