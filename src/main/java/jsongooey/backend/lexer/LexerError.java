package jsongooey.backend.lexer;

public record LexerError(String message, int line) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LexerError that = (LexerError) o;
        return this.message.equals(that.message) && this.line == that.line;
    }

    @Override
    public String toString() {
        return "Lexical error on line " + line + ": " + message;
    }
}