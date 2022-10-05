package be.effectlife.arvbotplus.models.conversions;

import static be.effectlife.arvbotplus.models.conversions.Temperature.C;
import static be.effectlife.arvbotplus.models.conversions.Temperature.F;

public interface Conversion {

    static float convertLinear(float value, float conversionSource, float conversionTarget) {
        return value / conversionSource * conversionTarget;
    }

    static float convertTemp(float value, Temperature source, Temperature target) {
        if (source == C) {
            if (target == F) {
                return value * 1.8f + 32;
            } else {
                return value + 273.15f;
            }
        }
        if (source == F) {
            if (target == C) {
                return (value - 32) * 0.5555555f;
            } else {
                return (value - 32) * 0.5555555f + 273.15f;
            }
        } else if (target == C) {
            return value - 273.15f;
        } else {
            return (value - 273.15f) * 1.8f + 32;
        }
    }
}
