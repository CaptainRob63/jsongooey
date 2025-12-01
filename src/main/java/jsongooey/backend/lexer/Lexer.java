package jsongooey.backend.lexer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static jsongooey.backend.lexer.TokenType.*;

public class Lexer {
    private final String source;
    private List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private List<LexerError> errors = new ArrayList<>();

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> getTokens() {
        return Collections.unmodifiableList(tokens);
    }

    public List<LexerError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    private void addToken(Token token) {
        tokens.add(token);
    }

    private void addToken(TokenType type) {
        switch (type) {
            case OPEN_BRACE ->  addToken(new Token(OPEN_BRACE, "{", null, line));
            case CLOSE_BRACE -> addToken(new Token(CLOSE_BRACE, "}", null, line));
            case OPEN_SQUARE_BRACKET -> addToken(new Token(OPEN_SQUARE_BRACKET, "[", null, line));
            case CLOSE_SQUARE_BRACKET -> addToken(new Token(CLOSE_SQUARE_BRACKET, "]", null, line));
            case COLON -> addToken(new Token(COLON, ":", null, line));
            case COMMA -> addToken(new Token(COMMA, ",", null, line));
            case TRUE -> addToken(new Token(TRUE, "true", true, line));
            case FALSE -> addToken(new Token(FALSE, "false", false, line));
            case NULL -> addToken(new Token(NULL, "null", null, line));
            default -> throw new IllegalArgumentException("Unexpected token type: " + type);
        }
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        if(isAtEnd()) return '\0';
        return source.charAt(current++);
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }
    private boolean match(char expected) {
        boolean matches = peek() == expected;
        if (matches) advance();
        return matches;
    }

    private boolean match(String expected) {
        for (Character c : expected.toCharArray()) {
            if (!match(c)) return false;
        }
        return true;
    }

    private String lexeme() {
        return source.substring(start, current);
    }

    private void string() {
        while (! (isAtEnd() || peek() == '"') ) {
            if (peek() == '\\') advance();
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            report("unterminated string");
            return;
        }

        advance(); // closing "

        String lexeme = lexeme();
        addToken(new Token(STRING, lexeme, lexeme.substring(1, lexeme.length() - 1), line));
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void matchDigits() {
        while (isDigit(peek())) advance();
    }

    private void matchAtLeastOneDigit() {
        if (!isDigit(peek())) {
            report("atleast one digit was expected");
            return;
        }
        matchDigits();
    }

    private void exponent() {
        boolean sign = match('+') || match('-');
        matchAtLeastOneDigit();
    }

    private void matchFractionalAndExponent() {
        boolean fractional = match('.');
        if (fractional) {
            matchAtLeastOneDigit();
        }

        boolean exponential = match('e') || match('E');
        if (exponential) {
            exponent();
        }
    }

    private void number(char currentChar) {
        if (currentChar == '-') currentChar = advance();

        if (currentChar == '0') {
            matchFractionalAndExponent();
        }

        else if (List.of('1','2','3','4','5','6','7','8','9').contains(currentChar)) {
            matchDigits();
            matchFractionalAndExponent();
        }

        else {
            report("invalid starting character for number");
            return;
        }

        String lexeme = lexeme();
        addToken(new Token(NUMBER, lexeme, Double.parseDouble(lexeme), line));

    }

    private void boolTrue() {

        if (!match("rue")) {
            report("invalid true keyword");
            return;
        }

        addToken(TRUE);
    }

    private void boolFalse() {
        if (!match("alse")) {
            report("invalid false keyword");
            return;
        }

        addToken(FALSE);
    }

    private void jsonNull() {
        if (!match("ull")) {
            report("invalid null keyword");
            return;
        }

        addToken(NULL);
    }

    public void lexTokens() {
        while (!isAtEnd()) {
            start = current;
            lexToken();
        }
        addToken(new Token(EOF, "",null, line));
    }

    private void lexToken() {
        char c = advance();

        try {

            switch (c) {
                case '{' -> addToken(OPEN_BRACE);
                case '}' -> addToken(CLOSE_BRACE);
                case '[' -> addToken(OPEN_SQUARE_BRACKET);
                case ']' -> addToken(CLOSE_SQUARE_BRACKET);
                case ':' -> addToken(COLON);
                case ',' -> addToken(COMMA);
                case '"' -> string();
                case 't' -> boolTrue();
                case 'f' -> boolFalse();
                case 'n' -> jsonNull();
                case '-' -> number(c);
                case '\t', '\r', ' ' -> {/* skip whitespace */}
                case '\n' -> line++;

                default -> {
                    if (isDigit(c)) {
                        number(c);
                    } else {
                        report("unexpected character: " + c);
                    }
                }

            }
        } catch (LexerErrorException _) {
            while (!isAtEnd() && advance() != '\n')/* skip line if error found */;
            line++;
        }

    }

    private void report(String message) throws LexerErrorException {
        errors.add(new LexerError(message, line));
        throw new LexerErrorException();
    }
}



