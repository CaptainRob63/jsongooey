package jsongooey.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static jsongooey.parser.TokenType.*;

public class Lexer {
    private String source;
    private List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int end = 0;
    private int line = 1;

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
        return source.charAt(current++);
    }

    private char peek() {
        return source.charAt(current);
    }
    private boolean match(char expected) {
        boolean matches = peek() == expected;
        if (matches) advance();
        return matches;
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd())  ; // TODO: error

        advance();

        String lexeme = source.substring(start, current);
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
            // TODO error
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
        if (currentChar == '0') {
            matchFractionalAndExponent();
        }

        else if (List.of('1','2','3','4','5','6','7','8','9').contains(currentChar)) {
            matchDigits();
            matchFractionalAndExponent();
        }

        else {
            // TODO error
        }

        String lexeme = source.substring(start, current);
        addToken(new Token(NUMBER, lexeme, Double.parseDouble(lexeme), line));

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
        switch (c) {
            case '{' -> addToken(new Token(OPEN_BRACE, "{", null, line));
            case '}' -> addToken(new Token(CLOSE_BRACE, "}", null, line));
            case '[' -> addToken(new Token(OPEN_SQUARE_BRACKET, "[", null, line));
            case ']' -> addToken(new Token(CLOSE_SQUARE_BRACKET, "]", null, line));
            case ':' -> addToken(new Token(COLON, ":", null, line));
            case ',' -> addToken(new Token(COMMA, ",", null, line));
            case '"' -> string();
            case '-' -> number(c);
            default ->  {
                if (isDigit(c)) {
                    number(c);
                } else {
                    // TODO: error
                    System.out.println("Unexpected character: " + c);
                }
            }
        }

    }
}

/*
    TODO:
    error
    boolean
 */

