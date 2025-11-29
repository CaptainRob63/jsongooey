package jsongooey.jsonmodel;

public class BooleanValue implements Value {
    private Boolean value;

    public BooleanValue(Boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
