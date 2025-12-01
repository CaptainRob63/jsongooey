package jsongooey.backend.parser;

import jsongooey.backend.jsonmodel.*;
import jsongooey.backend.lexer.Token;
import jsongooey.backend.lexer.TokenType;

import static jsongooey.backend.lexer.TokenType.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private boolean isAtEnd() {
        return current >= tokens.size();
    }

    private Token advance() {
        if (isAtEnd()) return tokens.get(current);
        return tokens.get(current++);
    }

    private Token peek() {
        if (isAtEnd()) return tokens.get(current);
        return tokens.get(current);
    }

    private Token peekPrevious() {
        return tokens.get(current - 1);
    }

    private boolean match(TokenType type) {
        if (peek().getType() != type)
            return false;
        else {
            advance();
            return true;
        }
    }

    private boolean check(TokenType... types) {
        for (TokenType type : types) {
            if (peek().getType() == type) return true;
        }
        return false;
    }

    private Token mustMatch(TokenType type, String errorMessage) {
        if (!match(type)) report(errorMessage);
        return peekPrevious();
    }

    private void skipTo(TokenType type) {
        while (peek().getType() != type) advance() ;
    }

    private Optional<StringValue> string() {
        if (match(STRING)) return Optional.of(new StringValue( (String) peekPrevious().getLiteral() ));
        return Optional.empty();
    }

    private Optional<NumberValue> number() {
        if (match(NUMBER)) return Optional.of(new NumberValue( (double) peekPrevious().getLiteral() ));
        return Optional.empty();
    }

    private Optional<BooleanValue> booleanValue() {
        if (check(FALSE, TRUE)) {
            advance();
            return Optional.of(new BooleanValue( (boolean) peekPrevious().getLiteral() ));
        }
        return Optional.empty();
    }

    private Optional<NullValue> nullValue() {
        if (match(NULL)) return Optional.of(new NullValue());
        return Optional.empty();
    }

    private Optional<ArrayValue> array() throws SyntaxErrorException {
        if (!match(OPEN_SQUARE_BRACKET)) return Optional.empty();

        ArrayValue array = new ArrayValue();

        Optional<Value> newValue = value();
        while(newValue.isPresent()) {
            array.addValue(newValue.get());

            if (!match(COMMA)) break;

            try {
                newValue = value();
            } catch (SyntaxErrorException _) {
                skipTo(COMMA);
            }
        }

        mustMatch(CLOSE_SQUARE_BRACKET, "array unclosed");

        return Optional.of(array);
    }

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

    private Optional<ObjectValue> object() throws SyntaxErrorException {
        if(!match(OPEN_BRACE)) return Optional.empty();

        ObjectValue newObject = new ObjectValue();
        Optional<Member> member = Optional.empty();
        try {
            member = member();
        } catch (SyntaxErrorException _) {
            skipTo(COMMA); // just assume theres
        }

        if (member.isPresent()) {
            newObject.addMember(member.get());

            while(match(COMMA)) {
                try {
                    member = member();
                } catch (SyntaxErrorException _) {
                    // TODO: error
                }

                if (member.isEmpty()) report("object has trailing comma");
                else newObject.addMember(member.get());
            }

        }

        mustMatch(CLOSE_BRACE, "object not terminated by closing brace");

        return Optional.of(newObject);
    }

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

    private void report(String message) throws SyntaxErrorException {
        errors.add(new SyntaxError(message, peek().getLine()));
        throw new SyntaxErrorException();
    }


    public void parse() {
        object = object().orElse(new ObjectValue());
    }

}

