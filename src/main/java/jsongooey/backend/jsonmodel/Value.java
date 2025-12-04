package jsongooey.backend.jsonmodel;

/**
 * all objects that represent a json value implement this interface
 * visitor pattern compatible
 */
public interface Value {
    <R> R accept(ValueVisitor<R> visitor);
}
