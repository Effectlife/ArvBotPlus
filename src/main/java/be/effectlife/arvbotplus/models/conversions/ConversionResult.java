package be.effectlife.arvbotplus.models.conversions;

public class ConversionResult {
    private float targetValue;
    private float sourceValue;
    private CType sourceType;
    private CType targetType;
    private String errorMessage;

    public ConversionResult(float sourceValue, float targetValue, CType sourceType, CType targetType) {
        this.sourceValue = sourceValue;
        this.targetValue = targetValue;
        this.sourceType = sourceType;
        this.targetType = targetType;
    }

    public ConversionResult(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public float getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(float sourceValue) {
        this.sourceValue = sourceValue;
    }

    public float getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(float targetValue) {
        this.targetValue = targetValue;
    }

    public CType getSourceType() {
        return sourceType;
    }

    public void setSourceType(CType sourceType) {
        this.sourceType = sourceType;
    }

    public CType getTargetType() {
        return targetType;
    }

    public void setTargetType(CType targetType) {
        this.targetType = targetType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
