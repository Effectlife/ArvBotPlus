package be.effectlife.arvbotplus.services.conversions.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class CType {
    private float conversionToBase;
    private String unit;

    public float getConversionToBase() {
        return conversionToBase;
    }

    public String getUnit() {
        return unit;
    }

    protected void setUnit(String unit) {
        this.unit = unit;
    }

    public void setConversionToBase(float conversionToBase) {
        this.conversionToBase = conversionToBase;
    }

    public static HashMap<String, List<CType>> getAllAvailableTypes() {
        HashMap<String, List<CType>> hashMap = new HashMap<>();
        hashMap.put(Length.getType(), Length.getAllValues());
        hashMap.put(Temperature.getType(), Temperature.getAllValues());
        hashMap.put(Volume.getType(), Volume.getAllValues());
        hashMap.put(Weight.getType(), Weight.getAllValues());
        return hashMap;
    }

    @Override
    public String toString() {
        return getUnit();
    }
}
