package jsongooey.frontend;

import jsongooey.backend.jsonmodel.ObjectValue;
import jsongooey.backend.jsonmodel.PrintVisitor;
import jsongooey.backend.lexer.Lexer;
import jsongooey.backend.parser.Parser;
import jsongooey.frontend.jsontree.JsonTree;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static jsongooey.frontend.jsontree.JsonTree.createRootNode;

/**
 * the main window of the application
 * <p>
 * has an RSyntaxTextArea for writing and displaying json files
 * the library JTextArea implementation is used syntax highlighting and editing utility
 * <p>
 * has a JTree implemented as a JsonTree with custom renderer to graphically display a json object
 * <p>
 * has import and export buttons and a textfield to write the filepath
 * <p>
 * has a parse button to run the backend parsing algorithm and change the JsonTree model
 */
public class JsonFrame extends JFrame {
    private JButton importButton = new JButton("Import");
    private JButton exportButton = new JButton("Export");
    private JButton parseButton = new JButton("Parse");
    private JTextField filePathField = new JTextField(30);

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
        bottomPanel.add(filePathField);
        bottomPanel.add(importButton);
        bottomPanel.add(exportButton);
        bottomPanel.add(parseButton);


        importButton.setBackground(darker);
        exportButton.setBackground(darker);
        parseButton.setBackground(darker);
        importButton.setForeground(Color.WHITE);
        exportButton.setForeground(Color.WHITE);
        parseButton.setForeground(Color.WHITE);

        importButton.addActionListener(e -> {
            String filePath = filePathField.getText();
            try {
                textArea.setText(Files.readString(Path.of(filePath)));
            } catch (IOException _) {
                JOptionPane.showMessageDialog(
                        null,
                        "File not found!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        exportButton.addActionListener(e -> {
            String filePath = filePathField.getText();
            try {
                Files.writeString(Path.of(filePath), jsonTree.getJsonObject().accept(new PrintVisitor()));
            } catch (IOException _) {
                JOptionPane.showMessageDialog(
                        null,
                        "File not found!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });


        parseButton.addActionListener(e -> {
            String text = textArea.getText();
            Lexer lexer = new Lexer(text);
            lexer.lexTokens();
            if (!lexer.getErrors().isEmpty()) {
                String lexErrorsMsg = lexer.getErrors().stream()
                        .map(err -> err.toString() + "\n")
                        .reduce(String::concat)
                        .get();

                JOptionPane.showMessageDialog(
                        null,
                        lexErrorsMsg,
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            Parser parser = new Parser(lexer.getTokens());
            parser.parse();

            if (!parser.getErrors().isEmpty()) {
                String parseErrorMsg = parser.getErrors().getFirst().toString();
                JOptionPane.showMessageDialog(
                        null,
                        parseErrorMsg,
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            jsonTree.setJsonModel(parser.getObject());
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



        JScrollPane textScroll = new JScrollPane(textArea);
        textScroll.setBackground(black);
        textScroll.setForeground(darker);

        JScrollPane treeScroll = new JScrollPane(jsonTree);
        treeScroll.setBackground(black);
        treeScroll.setForeground(darker);

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
