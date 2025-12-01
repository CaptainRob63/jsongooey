package jsongooey.frontend;

import jsongooey.backend.jsonmodel.ObjectValue;
import jsongooey.backend.jsonmodel.StringValue;
import jsongooey.backend.lexer.Lexer;
import jsongooey.backend.parser.Parser;
import jsongooey.frontend.jsontree.JsonTree;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

import static jsongooey.frontend.jsontree.JsonTree.createRootNode;

public class JsonFrame extends JFrame {
    private JButton importButton = new JButton("Import");
    private JButton exportButton = new JButton("Export");
    private JButton parseButton = new JButton("Parse");

    private JsonTree jsonTree;
    private RSyntaxTextArea textArea;

    public JsonFrame() {
        setTitle("JSON Gooey");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Color background = new Color(66, 66, 66);
        Color darker = new Color(39, 39, 39);
        Color black = new Color(11, 11, 11);



        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(800, 600));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(66, 66, 66));


        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(background);
        bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(importButton);
        bottomPanel.add(exportButton);
        bottomPanel.add(parseButton);

        importButton.setBackground(darker);
        exportButton.setBackground(darker);
        parseButton.setBackground(darker);
        importButton.setForeground(Color.WHITE);
        exportButton.setForeground(Color.WHITE);
        parseButton.setForeground(Color.WHITE);

        parseButton.addActionListener(e -> {
            String text = textArea.getText();
            Lexer lexer = new Lexer(text);
            lexer.lexTokens();
            Parser parser = new Parser(lexer.getTokens());
            parser.parse();
            jsonTree.setModel(new DefaultTreeModel(createRootNode(parser.getObject())));
        });

        textArea = new RSyntaxTextArea();
        textArea.setBackground(black);
        textArea.setForeground(Color.WHITE);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
        textArea.setCurrentLineHighlightColor(Color.BLACK);
        textArea.setEditable(true);

        jsonTree = new JsonTree(new ObjectValue());
        jsonTree.setBackground(darker);
        jsonTree.setForeground(Color.WHITE);

        BasicScrollBarUI scrollUI = new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                thumbColor = darker;
                trackColor = black;
            }
        };

        JScrollPane textScroll = new JScrollPane(textArea);
        textScroll.setBackground(black);
        textScroll.setForeground(darker);

        JScrollPane treeScroll = new JScrollPane(jsonTree);
        treeScroll.setBackground(black);
        treeScroll.setForeground(darker);


        JScrollBar v1 = textScroll.getVerticalScrollBar();
        JScrollBar h1 = textScroll.getHorizontalScrollBar();
        JScrollBar v2 = treeScroll.getVerticalScrollBar();
        JScrollBar h2 = treeScroll.getHorizontalScrollBar();

        v1.setUI(scrollUI);
        h1.setUI(scrollUI);
        v2.setUI(scrollUI);
        h2.setUI(scrollUI);

        JSplitPane middleSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, textScroll, treeScroll);
        middleSplitPane.setDividerSize(5);
        middleSplitPane.setResizeWeight(0.7);
        middleSplitPane.setBackground(background);

        mainPanel.add(middleSplitPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private static void makeIt() {
        JFrame frame = new JsonFrame();
        frame.setVisible(true);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(JsonFrame::makeIt);
    }


}
