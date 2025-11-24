package jsongooey.lexer;

import org.junit.Assert;
import org.junit.Test;

import static jsongooey.lexer.TokenType.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        String src = "{   \"sigma\" : -6.3}";

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

    @Test
    public void fromExample() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/example.json")) {
            Assert.assertNotNull(is);
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            Lexer lexer = new Lexer(content);
            lexer.lexTokens();

            System.out.println(lexer.getTokens());

        }
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
