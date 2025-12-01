package jsongooey.backend.jsonmodel;

public interface ValueVisitor<R> {
    R visit(BooleanValue boolValue);
    R visit(NumberValue numberValue);
    R visit(StringValue stringValue);
    R visit(NullValue nullValue);
    R visit(ObjectValue objectValue);
    R visit(ArrayValue arrayValue);
}
