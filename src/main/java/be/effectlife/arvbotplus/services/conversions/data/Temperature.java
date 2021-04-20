package be.effectlife.arvbotplus.services.conversions.data;

import java.util.Arrays;
import java.util.List;

public final class Temperature extends CType {
    public static Temperature K = new Temperature("K");
    public static Temperature F = new Temperature("F");
    public static Temperature C = new Temperature("C");

    private Temperature(String unit) {
        setUnit(unit);
    }

    public static List<CType> getAllValues() {
        return Arrays.asList(K, F, C);
    }

    public static String getType() {
        return "Temperature"; //TODO: Migrate to MessageKey
    }
}
