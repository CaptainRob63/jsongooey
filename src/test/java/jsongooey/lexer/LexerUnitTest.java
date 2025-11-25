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
    public void various() {
        String src = "true false null";

        var expected = List.of(
                TRUE,
                FALSE,
                NULL,
                EOF
        );

        assertTokenTypes(src, expected);
    }

    @Test
    public void unterminatedString() {
        String src = "{}}}[]\"awdol][[";

        assertHasError(src, new LexerError("unterminated string", 1));
    }

    @Test
    public void wrongIdentifiers() {
        String src = "tRue \n fal31 \n nual";

        assertHasError(src, new LexerError("invalid true keyword", 1));
        assertHasError(src, new LexerError("invalid false keyword", 2));
        assertHasError(src, new LexerError("invalid null keyword", 3));
    }

    @Test
    public void unexpectedChar() {
        String src = "[\"six\",\n\n \"seven\", ?]";

        assertHasError(src, new LexerError("unexpected character: ?", 3));
    }

    @Test
    public void wrongExponent() {
        String src = "\n62.23e";

        assertHasError(src, new LexerError("atleast one digit was expected", 2));
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

    private void assertHasError(String src, LexerError expected) {
        Lexer lexer = new Lexer(src);
        lexer.lexTokens();
        Assert.assertTrue(lexer.getErrors().contains(expected));
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
