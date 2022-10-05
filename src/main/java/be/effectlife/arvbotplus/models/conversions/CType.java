package be.effectlife.arvbotplus.models.conversions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CType {
    private float conversionToBase;
    private String unit;
    private String displayName;

    public static Map<String, List<CType>> getAllAvailableTypes() {
        HashMap<String, List<CType>> hashMap = new HashMap<>();
        hashMap.put(Length.getType(), Length.getAllValues());
        hashMap.put(Temperature.getType(), Temperature.getAllValues());
        hashMap.put(Volume.getType(), Volume.getAllValues());
        hashMap.put(Weight.getType(), Weight.getAllValues());
        return hashMap;
    }

    public float getConversionToBase() {
        return conversionToBase;
    }

    public void setConversionToBase(float conversionToBase) {
        this.conversionToBase = conversionToBase;
    }

    public String getUnit() {
        return unit;
    }

    protected void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDisplayName() {
        return displayName;
    }

    protected void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return getUnit();
    }
}
