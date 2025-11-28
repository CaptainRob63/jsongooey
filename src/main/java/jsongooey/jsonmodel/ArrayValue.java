package jsongooey.jsonmodel;

import java.util.List;

public class ArrayValue implements Value {
    private List<Value> array;

    public List<Value> getArray() {
        return array;
    }
    public void addValue(Value value) {
        this.array.add(value);
    }
}
