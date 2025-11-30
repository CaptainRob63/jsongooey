package jsongooey.jsonmodel;

import java.util.ArrayList;
import java.util.List;

public class ArrayValue implements Value {
    private List<Value> array = new ArrayList<>();

    public List<Value> getArray() {
        return array;
    }
    public ArrayValue addValue(Value value) {
        this.array.add(value);
        return this;
    }

    public ArrayValue addValue(String value) {
        this.array.add(new StringValue(value));
        return this;
    }

    public ArrayValue addValue(double value) {
        this.array.add(new NumberValue(value));
        return this;
    }

    public ArrayValue addValue(boolean value) {
        this.array.add(new BooleanValue(value));
        return this;
    }

    @Override
    public String toString() {
        return array.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ArrayValue arrayValue = (ArrayValue) obj;
        return array.equals(arrayValue.array);
    }

    @Override
    public int hashCode() {
        return array.hashCode();
    }
}
