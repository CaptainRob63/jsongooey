package jsongooey.backend.parser;

import jsongooey.backend.jsonmodel.*;
import jsongooey.backend.lexer.Token;
import jsongooey.backend.lexer.TokenType;

import static jsongooey.backend.lexer.TokenType.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *  Constructed with a List of Token objects.
 *  After calling parse() fills the object field with the top level object, and the errors field with Syntax errors found.
 *  currently only the first syntax error is reported.
 *  Does not signal when error occured, must be checked manually.
 */
public class Parser {
    private final List<Token> tokens;
    private ObjectValue object =  new ObjectValue();

    private int current = 0;

    private List<SyntaxError> errors = new ArrayList<>();

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public ObjectValue getObject() {
        return object;
    }

    public List<SyntaxError> getErrors() {
        return errors;
    }

    /**
     * @return whether the current pointer has reached the end of the source code
     */
    private boolean isAtEnd() {
        return current >= tokens.size();
    }

    /**
     * advances the current pointer, consuming a token
     * @return the token consumed
     */
    private Token advance() {
        if (isAtEnd()) return tokens.get(current);
        return tokens.get(current++);
    }

    /**
     * @return the next token to be consumed
     */
    private Token peek() {
        if (isAtEnd()) return tokens.get(current);
        return tokens.get(current);
    }

    /**
     * @return the token that was just consumed
     */
    private Token peekPrevious() {
        return tokens.get(current - 1);
    }

    /**
     * consumes a token only if it's a given type
     * @param type the type to match against
     * @return whether a token was consumed
     */
    private boolean match(TokenType type) {
        if (peek().getType() != type)
            return false;
        else {
            advance();
            return true;
        }
    }

    /**
     * check if the next token is of a number of given types. does not consume
     * @param types the types to check against
     * @return whether the next token is any type of types
     */
    private boolean check(TokenType... types) {
        for (TokenType type : types) {
            if (peek().getType() == type) return true;
        }
        return false;
    }

    /**
     * consumes next token of expected type, if unexpected adds error with message errorMessage to field errors and throws exception
     * @param type type to check against
     * @param errorMessage message of error added to errors if the token does not match
     * @return the next token if matched
     * @throws SyntaxErrorException if the next token is not of expected type
     */
    private Token mustMatch(TokenType type, String errorMessage) {
        if (!match(type)) report(errorMessage);
        return peekPrevious();
    }

    /**
     * skips the current pointer to the next token of type
     * @param type type of token to skip to
     */
    private void skipTo(TokenType type) {
        while (peek().getType() != type) advance() ;
    }

    /**
     * parses a string token
     * @return Optional of a StringValue object if successful, else empty
     */
    private Optional<StringValue> string() {
        if (match(STRING)) return Optional.of(new StringValue( (String) peekPrevious().getLiteral() ));
        return Optional.empty();
    }

    /**
     * parses a number token
     * @return Optional of a NumberValue object if successful, else empty
     */
    private Optional<NumberValue> number() {
        if (match(NUMBER)) return Optional.of(new NumberValue( (double) peekPrevious().getLiteral() ));
        return Optional.empty();
    }

    /**
     * parses a boolean token
     * @return Optional of a BooleanValue object if successful, else empty
     */
    private Optional<BooleanValue> booleanValue() {
        if (check(FALSE, TRUE)) {
            advance();
            return Optional.of(new BooleanValue( (boolean) peekPrevious().getLiteral() ));
        }
        return Optional.empty();
    }

    /**
     * parses a null token
     * @return Optional of a NullValue object if successful, else empty
     */
    private Optional<NullValue> nullValue() {
        if (match(NULL)) return Optional.of(new NullValue());
        return Optional.empty();
    }

    /**
     * parses an array
     * @return Optional of a ArrayValue object if successful, else empty
     */
    private Optional<ArrayValue> array() throws SyntaxErrorException {
        if (!match(OPEN_SQUARE_BRACKET)) return Optional.empty();

        ArrayValue array = new ArrayValue();

        Optional<Value> newValue = Optional.empty();

        try {
            newValue = value();
        } catch (SyntaxErrorException e) {
            return Optional.of(array);
        }

        while(newValue.isPresent()) {
            array.addValue(newValue.get());

            if (!match(COMMA)) break;

            try {
                newValue = value();
            } catch (SyntaxErrorException _) {
                return Optional.of(array);
            }
        }

        mustMatch(CLOSE_SQUARE_BRACKET, "array unclosed");

        return Optional.of(array);
    }

    /**
     * parses an object member (key value pair), meant to be used in parsing an object
     * @return Optional of a Member object if successful, else empty
     */
    private Optional<Member> member() throws SyntaxErrorException {
        if (!match(STRING)) return Optional.empty();
        String key = (String) peekPrevious().getLiteral();

        mustMatch(COLON, "colon not found after key name");

        Optional<Value> value = value();
        if (value.isPresent()) {
            return Optional.of(new Member(key, value.get()));
        } else {
            report("value not found in object member");
            return Optional.empty(); // unreachable
        }
    }

    /**
     * parses an object
     * @return Optional of a ObjectValue object if successful, else empty
     */
    private Optional<ObjectValue> object() throws SyntaxErrorException {
        if(!match(OPEN_BRACE)) return Optional.empty();

        ObjectValue newObject = new ObjectValue();
        Optional<Member> member = Optional.empty();
        try {
            member = member();
        } catch (SyntaxErrorException _) {
            return Optional.of(newObject);
        }

        if (member.isPresent()) {
            newObject.addMember(member.get());

            while(match(COMMA)) {
                try {
                    member = member();
                } catch (SyntaxErrorException _) {
                    return Optional.of(newObject);
                }

                if (member.isEmpty()) report("object has trailing comma");
                else newObject.addMember(member.get());
            }

        }

        mustMatch(CLOSE_BRACE, "object not terminated by closing brace");

        return Optional.of(newObject);
    }

    /**
     * parses a value (in key value pair aka member)
     * @return Optional of a Value object if successful, else empty
     */
    private Optional<Value> value() {
        Optional<Value> newValue;

        newValue = object().map(v -> (Value) v);
        if (newValue.isPresent()) return newValue;

        newValue = array().map(v -> (Value) v);
        if (newValue.isPresent()) return newValue;

        newValue = number().map(v -> (Value) v);
        if (newValue.isPresent()) return newValue;

        newValue = string().map(v -> (Value) v);
        if (newValue.isPresent()) return newValue;

        newValue = booleanValue().map(v -> (Value) v);
        if (newValue.isPresent()) return newValue;

        newValue = nullValue().map(v -> (Value) v);
        if (newValue.isPresent()) return newValue;

        return Optional.empty();
    }

    /**
     * adds a SyntaxError object to errors field and throws exception
     * @param message error messsage
     * @throws SyntaxErrorException
     */
    private void report(String message) throws SyntaxErrorException {
        errors.add(new SyntaxError(message, peek().getLine()));
        throw new SyntaxErrorException();
    }


    /**
     * wrapper parse function. parses the top level object
     */
    public void parse() {
        object = object().orElse(new ObjectValue());
    }

}

