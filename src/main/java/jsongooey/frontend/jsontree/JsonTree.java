package jsongooey.frontend.jsontree;

import jsongooey.backend.jsonmodel.ObjectValue;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;

public class JsonTree extends JTree {
    private ObjectValue jsonObject;

    public JsonTree(ObjectValue jsonObject) {
        super(createRootNode(jsonObject));
        this.jsonObject = jsonObject;
        setCellRenderer(new JsonTreeRenderer());
    }

    public ObjectValue getJsonObject() {
        return jsonObject;
    }

    public void setJsonModel (ObjectValue objectValue) {
        this.jsonObject = objectValue;
        this.setModel(new DefaultTreeModel(createRootNode(objectValue)));
    }

    public static JsonTreeNode createRootNode(ObjectValue jsonObject) {

        JsonTreeNode root = new JsonTreeNode("root", "object");

        for (var child : jsonObject.accept(new GetNodesVisitor())) {
            root.add(child);
        }
        return root;
    }
}
