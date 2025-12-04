package jsongooey.backend.parser;

/**
 * @param message
 * @param line
 */
public record SyntaxError(String message, int line) {
    @Override
    public String toString() {
        return "Syntax error on line " + line + ": " + message;
    }
}
