package jsongooey.jsonmodel;

public class NullValue implements Value {

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NullValue;
    }

    @Override
    public int hashCode() {
        return 0;
    }

}
