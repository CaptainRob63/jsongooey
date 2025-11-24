package jsongooey.lexer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static jsongooey.lexer.TokenType.*;

public class Lexer {
    private String source;
    private List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private List<LexerError> errors = new ArrayList<>();

    public Lexer(String source) {
        this.source = source.replaceAll("[\t\r ]", "");
    }

    public List<Token> getTokens() {
        return Collections.unmodifiableList(tokens);
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
            default -> throw new IllegalArgumentException("Unexpected token type: " + type);
        }
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        if (source.charAt(current) == '\n') line++;
        return source.charAt(current++);
    }

    private char advance(int times) {
        for (int i = 0; i < times - 1; i++) {
            advance();
        }
        return advance();
    }

    private char peek() {
        return source.charAt(current);
    }
    private boolean match(char expected) {
        boolean matches = peek() == expected;
        if (matches) advance();
        return matches;
    }

    private String lexeme() {
        return source.substring(start, current);
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            report("unterminated string");
            return;
        }

        advance(); // closing "

        String lexeme = lexeme();
        addToken(new Token(STRING, lexeme, lexeme, line));
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
        if (!match('-')) match('+');
        matchAtLeastOneDigit();
    }

    private void matchFractionalAndExponent() {
        if (match('.')) {
            matchAtLeastOneDigit();
            exponent();
        }
        else if (match('e') || match('E')) {
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
        advance(3);

        if (!lexeme().equals("true")) {
            report("invalid true keyword");
            return;
        }

        addToken(new Token(TRUE, "true", true, line));
    }

    private void boolFalse() {
        advance(4);

        if (!lexeme().equals("false")) {
            report("invalid false keyword");
            return;
        }

        addToken(new Token(FALSE, "false", false, line));
    }

    private void jsonNull() {
        advance(3);

        if (!lexeme().equals("null")) {
            report("invalid null keyword");
            return;
        }

        addToken(new Token(NULL, "null", null, line));
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
                case '{' -> addToken(new Token(OPEN_BRACE, "{", null, line));
                case '}' -> addToken(new Token(CLOSE_BRACE, "}", null, line));
                case '[' -> addToken(new Token(OPEN_SQUARE_BRACKET, "[", null, line));
                case ']' -> addToken(new Token(CLOSE_SQUARE_BRACKET, "]", null, line));
                case ':' -> addToken(new Token(COLON, ":", null, line));
                case ',' -> addToken(new Token(COMMA, ",", null, line));
                case '"' -> string();
                case 't' -> boolTrue();
                case 'f' -> boolFalse();
                case 'n' -> jsonNull();
                case '-' -> number(c);
                default -> {
                    if (isDigit(c)) {
                        number(c);
                    } else {
                        report("Unexpected character: " + c);
                    }
                }
            }
        } catch (LexerErrorException e) {
            while (advance() != '\n');
        }

    }

    private void report(String message) throws RuntimeException {
        errors.add(new LexerError(message, line));
        throw new RuntimeException();
    }
}


