package jsongooey.backend.jsonmodel;

/**
 * visitor interface on any class that implements the Value interface
 * @param <R> the return value of the operation the pattern implements
 */
public interface ValueVisitor<R> {
    R visit(BooleanValue boolValue);
    R visit(NumberValue numberValue);
    R visit(StringValue stringValue);
    R visit(NullValue nullValue);
    R visit(ObjectValue objectValue);
    R visit(ArrayValue arrayValue);
}
