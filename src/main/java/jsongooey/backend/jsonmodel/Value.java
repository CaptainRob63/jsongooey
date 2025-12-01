package jsongooey.backend.jsonmodel;

public interface Value {
    <R> R accept(ValueVisitor<R> visitor);
}
