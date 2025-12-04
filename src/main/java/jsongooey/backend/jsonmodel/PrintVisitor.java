package jsongooey.backend.jsonmodel;


/**
 * Visitor for serializing Value objects
 */
public class PrintVisitor implements ValueVisitor<String> {
    private int indent;
    private final String indentString;

    public PrintVisitor(String indentString) {
        this.indentString = indentString;
    }

    public PrintVisitor() {
        this.indentString = "  ";
    }


    @Override
    public String visit(BooleanValue boolValue) {
        return String.valueOf(boolValue.getValue());
    }

    @Override
    public String visit(NumberValue numberValue) {
        double number = numberValue.getValue();
        return (number % 1 == 0) ? String.valueOf((int) number) : String.valueOf(number);
    }

    @Override
    public String visit(StringValue stringValue) {
        return String.valueOf( "\"" + stringValue.getValue() + "\"");
    }

    @Override
    public String visit(NullValue nullValue) {
        return String.valueOf("null");
    }

    @Override
    public String visit(ObjectValue objectValue) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("{\n");
        indent++;

        var members = objectValue.getMembers();
        for (var entry : members.entrySet()) {
            buffer.append("\t".repeat(indent));
            buffer.append(String.format("\"%s\": %s,\n", entry.getKey(), entry.getValue().accept(this)));
        }

        if (!members.isEmpty()) {
            buffer.deleteCharAt(buffer.length() - 2);
        }

        indent--;
        buffer.append("\t".repeat(indent));
        buffer.append("}");

        return buffer.toString();
    }

    @Override
    public String visit(ArrayValue arrayValue) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[\n");
        indent++;
        for (var value : arrayValue.getArray()) {
            buffer.append("\t".repeat(indent));
            buffer.append(String.format("%s,\n", value.accept(this)));
        }

        if (!arrayValue.getArray().isEmpty()) {
            buffer.deleteCharAt(buffer.length() - 2);
        }

        indent--;
        buffer.append("\t".repeat(indent));
        buffer.append("]");


        return buffer.toString();
    }
}
