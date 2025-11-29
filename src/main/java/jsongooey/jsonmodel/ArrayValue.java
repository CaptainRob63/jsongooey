package jsongooey.jsonmodel;

import java.util.ArrayList;
import java.util.List;

public class ArrayValue implements Value {
    private List<Value> array = new ArrayList<>();

    public List<Value> getArray() {
        return array;
    }
    public void addValue(Value value) {
        this.array.add(value);
    }
}
