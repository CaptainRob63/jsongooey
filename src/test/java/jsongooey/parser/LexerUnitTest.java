package jsongooey.parser;

import org.junit.Assert;
import org.junit.Test;

import jsongooey.parser.*;
import static jsongooey.parser.TokenType.*;

import java.util.List;


public class LexerUnitTest {
    @Test
    public void hello() {
        String src = "{}[]:";

        var expected = List.of(
                OPEN_BRACE,
                CLOSE_BRACE,
                OPEN_SQUARE_BRACKET,
                CLOSE_SQUARE_BRACKET,
                COLON,
                EOF
        );

        assertTokenTypes(src, expected);
    }


    @Test
    public void crazy() {
        String src = "{   \"sigma\" : 6.3}";

        var expected = List.of(
                OPEN_BRACE,
                STRING,
                COLON,
                NUMBER,
                CLOSE_BRACE,
                EOF
        );

        assertTokenTypes(src, expected);
    }

    private void assertTokenTypes(String src, List<TokenType> expected) {
        var lexer = new Lexer(src);
        lexer.lexTokens();

        Assert.assertEquals(expected, lexer.getTokens().stream().map(Token::getType).toList());
    }

    private void assertTokens(String src, List<Token> expected) {
        var lexer = new Lexer(src);
        lexer.lexTokens();

        Assert.assertEquals(expected, lexer.getTokens());
    }
}
