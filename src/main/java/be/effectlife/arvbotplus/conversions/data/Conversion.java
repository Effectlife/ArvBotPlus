package be.effectlife.arvbotplus.conversions.data;

import static be.effectlife.arvbotplus.conversions.data.Temperature.*;

public abstract class Conversion {
    /*
    M(1f), CM(100f),
    FT(3.28084f), YARD(1.09361f), MILE(0.000621371f);
    */
    public static float convertLinear(float value, float conversionSource, float conversionTarget) throws ArithmeticException {
        return value / conversionSource * conversionTarget;
    }

    public static float convertTemp(float value, Temperature source, Temperature target) {
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
