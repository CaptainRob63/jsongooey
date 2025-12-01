package jsongooey.frontend.jsontree;

import jsongooey.backend.jsonmodel.ObjectValue;

import javax.swing.*;

public class JsonTree extends JTree {
    private ObjectValue jsonObject;

    public JsonTree(ObjectValue jsonObject) {
        super(createRootNode(jsonObject));
        this.jsonObject = jsonObject;
        setCellRenderer(new JsonTreeRenderer());
    }

    private static JsonTreeNode createRootNode(ObjectValue jsonObject) {

        JsonTreeNode root = new JsonTreeNode("root", "object");

        for (var child : jsonObject.accept(new GetNodesVisitor())) {
            root.add(child);
        }
        return root;
    }
}
