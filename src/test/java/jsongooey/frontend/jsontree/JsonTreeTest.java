package jsongooey.frontend.jsontree;

import jsongooey.backend.jsonmodel.ObjectValue;
import jsongooey.backend.lexer.Lexer;
import jsongooey.backend.parser.Parser;
import org.junit.Assert;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static jsongooey.Util.readResourceToString;

public class JsonTreeTest extends JFrame {
    private JPanel contentPane = new JPanel();
    private JsonTree tree;

    public JsonTreeTest() throws IOException {
        String content = readResourceToString("/valid.json");

        Lexer lexer = new Lexer(content);
        lexer.lexTokens();
        Assert.assertTrue(lexer.getErrors().isEmpty());
        Parser parser = new Parser(lexer.getTokens());
        parser.parse();

        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(Color.gray);
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tree = new JsonTree(parser.getObject());

        var scrollPane = new JScrollPane(tree);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        setPreferredSize(new Dimension(800, 600));
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) throws IOException {
        JsonTreeTest jsonTreeTest = new JsonTreeTest();
        jsonTreeTest.setVisible(true);
    }
}
