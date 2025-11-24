package jsongooey.lexer;

public record LexerError(String message, int line) {
    @Override
    public String toString() {
        return "Error on line " + line + ": " + message;
    }
}