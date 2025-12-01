package jsongooey.backend.lexer;

public enum TokenType {
    OPEN_BRACE,
    CLOSE_BRACE,
    OPEN_SQUARE_BRACKET,
    CLOSE_SQUARE_BRACKET,
    COLON,
    COMMA,
    TRUE,
    FALSE,
    NULL,

    NUMBER,
    STRING,

    EOF
}
