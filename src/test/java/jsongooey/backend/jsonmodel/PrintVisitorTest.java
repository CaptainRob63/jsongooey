package jsongooey.backend.jsonmodel;

import jsongooey.backend.lexer.Lexer;
import jsongooey.backend.parser.Parser;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static jsongooey.Util.readResourceToString;

public class PrintVisitorTest {

    @Test
    public void testPrintVisitor() throws IOException {
        String content = readResourceToString("/valid.json");

        Lexer lexer = new Lexer(content);
        lexer.lexTokens();
        Parser parser = new Parser(lexer.getTokens());
        parser.parse();

        String printed = parser.getObject().accept(new PrintVisitor());
        System.out.println(printed);

    }
}
