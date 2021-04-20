package be.effectlife.arvbotplus.services;

import be.effectlife.arvbotplus.services.conversions.data.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConversionService {
    private final List<CType> types;

    {
        types = new ArrayList<>(Length.getAllValues());
        types.addAll(Temperature.getAllValues());
        types.addAll(Volume.getAllValues());
        types.addAll(Weight.getAllValues());
    }

    public ConversionResult convert(float value, CType sourceType, CType targetType) {
        if (sourceType == null || targetType == null || sourceType.getClass() != targetType.getClass()) {
            return null;
        }
        if (sourceType instanceof Temperature) {
            return new ConversionResult(value, Conversion.convertTemp(value, (Temperature) sourceType, (Temperature) targetType), sourceType, targetType);
        }
        return new ConversionResult(value, Conversion.convertLinear(value, sourceType.getConversionToBase(), targetType.getConversionToBase()), sourceType, targetType);
    }

    public CType getCTypeFromString(String type) {
        return types.stream().filter(cType -> cType.getUnit().toUpperCase(Locale.ROOT).equals(type.toUpperCase(Locale.ROOT))).findFirst().orElse(null);
    }
}
