package jsongooey.backend.jsonmodel;

public class NullValue implements Value {

    @Override
    public <R> R accept(ValueVisitor<R> visitor) {
        return visitor.visit(this);
    }

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
