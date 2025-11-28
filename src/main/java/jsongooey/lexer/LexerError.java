package jsongooey.lexer;

public record LexerError(String message, int line) {
    @Override
    public String toString() {
        return "Lexical error on line " + line + ": " + message;
    }
}