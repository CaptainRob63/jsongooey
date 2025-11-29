package jsongooey.parser;

import jsongooey.lexer.Lexer;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ParserUnitTest {

    @Test
    public void testParse() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/example.json")) {
            Assert.assertNotNull(is);
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            Lexer lexer = new Lexer(content);
            lexer.lexTokens();

            Assert.assertTrue(lexer.getErrors().isEmpty());

            Parser parser = new Parser(lexer.getTokens());
            parser.parse();

            System.out.println(parser.getObject());
        }
    }
}
