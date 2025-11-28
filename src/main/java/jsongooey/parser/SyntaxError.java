package jsongooey.parser;

public record SyntaxError(String message, int line) {
    @Override
    public String toString() {
        return "Syntax error on line " + line + ": " + message;
    }
}
