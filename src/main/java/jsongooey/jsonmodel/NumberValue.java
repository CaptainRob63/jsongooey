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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        NumberValue other = (NumberValue) obj;
        return other.value == this.value;
    }

    @Override
    public int hashCode() {
        return Double.valueOf(value).hashCode();
    }
}
