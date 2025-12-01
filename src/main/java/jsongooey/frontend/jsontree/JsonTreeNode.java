package jsongooey.frontend.jsontree;

import jsongooey.backend.jsonmodel.Value;

import javax.swing.tree.DefaultMutableTreeNode;

public class JsonTreeNode extends DefaultMutableTreeNode {
    private String label;
    private String sublabel;

    public JsonTreeNode(String label, String sublabel)  {
        this.label = label;
        this.sublabel = sublabel;
    }

    public String getLabel() {
        return label;
    }

    public String getSublabel() {
        return sublabel;
    }

    @Override
    public String toString() {
        return  label + ": " + sublabel;
    }
}