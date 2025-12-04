package jsongooey.backend.lexer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static jsongooey.backend.lexer.TokenType.*;

/**
 * constructed with the source json code as a String.
 * after calling lexTokens() fills tokens object and adds lexical errors to errors list
 */
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

    /**
     * wrapper function that adds the non-literal tokens just by specifying the type
     * @param type
     */
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

    /**
     * @return whether the current pointer is at the end of the source code
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * consumes the next character
     * @return the character consumed
     */
    private char advance() {
        if(isAtEnd()) return '\0';
        return source.charAt(current++);
    }

    /**
     * @return the next character
     */
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    /**
     * consumes the next character only if it is the expected character
     * @param expected
     * @return whether the next token was consumed
     */
    private boolean match(char expected) {
        boolean matches = peek() == expected;
        if (matches) advance();
        return matches;
    }

    /**
     * consumes the next sequence of characters only if they match a string
     * @param expected
     * @return whether the whole string was consumed
     */
    private boolean match(String expected) {
        for (Character c : expected.toCharArray()) {
            if (!match(c)) return false;
        }
        return true;
    }

    /**
     * @return the current lexeme outlined by the start and current pointers
     */
    private String lexeme() {
        return source.substring(start, current);
    }

    /**
     * lexes a string literal
     * @throws LexerErrorException if the string was unterminated
     */
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

    /**
     * consumes characters while they are digits
     */
    private void matchDigits() {
        while (isDigit(peek())) advance();
    }

    /**
     * consumes characters while they are digits
     * expects at least one digit
     * @throws LexerErrorException if the next token isn't a digit
     */
    private void matchAtLeastOneDigit() throws LexerErrorException {
        if (!isDigit(peek())) {
            report("at least one digit was expected");
            return;
        }
        matchDigits();
    }

    /**
     * matches the exponent part of a number, after 'E' or 'e'
     * expects exponent to be valid
     */
    private void exponent() throws LexerErrorException {
        boolean sign = match('+') || match('-');
        matchAtLeastOneDigit();
    }

    /**
     * matches the fractional and exponent part of a number
     * if a point is found, expects fractional part to match
     * if an 'e' or 'E' is found, expects an exponential part to match
     */
    private void matchFractionalAndExponent() throws LexerErrorException {
        boolean fractional = match('.');
        if (fractional) {
            matchAtLeastOneDigit();
        }

        boolean exponential = match('e') || match('E');
        if (exponential) {
            exponent();
        }
    }

    /**
     * parses a number
     * @param currentChar im a bad programmer.
     */
    private void number(char currentChar) throws LexerErrorException {
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

    /**
     * matches the string "rue"
     * called after a 't' is found
     */
    private void boolTrue() throws LexerErrorException {

        if (!match("rue")) {
            report("invalid true keyword");
            return;
        }

        addToken(TRUE);
    }

    /**
     * matches the string "alse".
     * called after an 'f' is found
     */
    private void boolFalse() throws LexerErrorException {
        if (!match("alse")) {
            report("invalid false keyword");
            return;
        }

        addToken(FALSE);
    }
    /**
     * matches the string "null".
     * called after an 'n' is found.
     */
    private void jsonNull() throws LexerErrorException {
        if (!match("ull")) {
            report("invalid null keyword");
            return;
        }

        addToken(NULL);
    }

    /**
     * lexes the given string
     */
    public void lexTokens() {
        while (!isAtEnd()) {
            start = current;
            lexToken();
        }
        addToken(new Token(EOF, "",null, line));
    }

    /**
     * lexes a single token
     * handles a lexical error by skipping the current line to find more
     */
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

    /**
     * adds a LexerError to errors and throws
     * @param message
     * @throws LexerErrorException
     */
    private void report(String message) throws LexerErrorException {
        errors.add(new LexerError(message, line));
        throw new LexerErrorException();
    }
}



