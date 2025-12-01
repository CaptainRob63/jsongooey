package jsongooey.backend.parser;

import jsongooey.backend.jsonmodel.ArrayValue;
import jsongooey.backend.jsonmodel.NullValue;
import jsongooey.backend.jsonmodel.ObjectValue;
import jsongooey.backend.parser.Parser;
import jsongooey.backend.lexer.Lexer;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static jsongooey.Util.readResourceToString;

public class ParserUnitTest {

    @Test
    public void validParse() throws IOException {
            String content = readResourceToString("/valid.json");

            Lexer lexer = new Lexer(content);
            lexer.lexTokens();

            Assert.assertTrue(lexer.getErrors().isEmpty());

            Parser parser = new Parser(lexer.getTokens());
            parser.parse();

            System.out.println(parser.getObject());
    }

    @Test
    public void simpleParse() throws IOException {
        String content = readResourceToString("/simple.json");

        Lexer lexer = new Lexer(content);
        lexer.lexTokens();
        Assert.assertTrue(lexer.getErrors().isEmpty());
        Parser parser = new Parser(lexer.getTokens());
        parser.parse();

        ArrayValue array = new ArrayValue()
                .addValue(1)
                .addValue("two")
                .addValue(true);

        ObjectValue object = new ObjectValue()
                .addMember("key_a","nested")
                .addMember("key_b", 42);

        ObjectValue expected = new ObjectValue()
                .addMember("string", "Hello, JSON!")
                .addMember("number", 3.14159)
                .addMember("true",true)
                .addMember("false", false)
                .addMember("null", new NullValue())
                .addMember("array", array)
                .addMember("array", array)
                .addMember("object", object);

        Assert.assertEquals(expected, parser.getObject());
    }

    @Test
    public void equalsTest() {
        ObjectValue object1 = new ObjectValue()
                .addMember("1", 1)
                .addMember("arr", new ArrayValue().addValue(1).addValue("2").addValue(true))
                .addMember("object", new ObjectValue());

        ObjectValue object2 = new ObjectValue()
                .addMember("1", 1)
                .addMember("arr", new ArrayValue().addValue(1).addValue("2").addValue(true))
                .addMember("object", new ObjectValue());

        Assert.assertEquals(object1, object2);
    }

}
