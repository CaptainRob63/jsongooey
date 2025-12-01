package jsongooey.frontend.jsontree;

import jsongooey.backend.jsonmodel.*;

public class GetValueTypeVisitor implements ValueVisitor<String> {

    @Override
    public String visit(BooleanValue boolValue) {
        return "bool";
    }

    @Override
    public String visit(NumberValue numberValue) {
        return "number";
    }

    @Override
    public String visit(StringValue stringValue) {
        return "string";
    }

    @Override
    public String visit(NullValue nullValue) {
        return "null";
    }

    @Override
    public String visit(ObjectValue objectValue) {
        return "object";
    }

    @Override
    public String visit(ArrayValue arrayValue) {
        return "array";
    }
}
