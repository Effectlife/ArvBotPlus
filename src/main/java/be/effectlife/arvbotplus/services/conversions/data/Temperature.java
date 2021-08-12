package be.effectlife.arvbotplus.services.conversions.data;

import be.effectlife.arvbotplus.loading.MessageKey;
import be.effectlife.arvbotplus.loading.MessageProperties;

import java.util.Arrays;
import java.util.List;

public final class Temperature extends CType {
    public static Temperature K = new Temperature("K", "°K");
    public static Temperature F = new Temperature("F", "°F");
    public static Temperature C = new Temperature("C", "°C");

    private Temperature(String unit, String displayName) {
        setUnit(unit);
        setDisplayName(displayName);
    }

    public static List<CType> getAllValues() {
        return Arrays.asList(K, F, C);
    }

    public static String getType() {
        return MessageProperties.getString(MessageKey.CONVERSIONS_TYPE_TEMPERATURE);
    }
}
