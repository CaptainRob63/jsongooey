package jsongooey.jsonmodel;

public class NumberValue implements Value {
    private double value;

    public NumberValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
