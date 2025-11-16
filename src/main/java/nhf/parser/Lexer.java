package nhf.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static nhf.parser.TokenType.*;

public class Lexer {
    private String source;
    private List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int end = 0;
    private int line = 1;

    public Lexer(String source) {
        this.source = source;
    }

    public void lex() {}

    public List<Token> getTokens() {
        return Collections.unmodifiableList(tokens);
    }

    private void addToken(Token token) {
        tokens.add(token);
    }

    private boolean isAtEnd() {
        return current >= end;
    }

    private char advance() {
        return source.charAt(current++);
    }

    private char peek() {
        return source.charAt(current);
    }

    private List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        addToken(new Token(EOF, "",null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '{' -> addToken(new Token(OPEN_BRACE, "{", null, line));
            case '}' -> addToken(new Token(CLOSE_BRACE, "}", null, line));
            case '[' -> addToken(new Token(OPEN_SQUARE_BRACKET, "[", null, line));
            case ']' -> addToken(new Token(CLOSE_SQUARE_BRACKET, "]", null, line));
            case ':' -> addToken(new Token(COLON, ":", null, line));
            case ',' -> addToken(new Token(COMMA, ",", null, line));
            case '"' -> addToken(new Token(QUOTE, "\"", null, line));
        }
    }
}

