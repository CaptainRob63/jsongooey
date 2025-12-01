package jsongooey.frontend.jsontree;

import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class JsonTreeRenderer extends JPanel implements TreeCellRenderer {
    private JLabel label = new JLabel();
    private JLabel subLabel = new JLabel();

    public JsonTreeRenderer() {
        subLabel.setForeground(Color.gray);
        add(label);
        add(subLabel);
        setOpaque(false);
    }


    @Override
    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean selected,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus)
    {
        if (value instanceof JsonTreeNode) {
            JsonTreeNode node = (JsonTreeNode) value;
            label.setText(node.getLabel());
            subLabel.setText(node.getSublabel());
        }

        return this;
    }
}
