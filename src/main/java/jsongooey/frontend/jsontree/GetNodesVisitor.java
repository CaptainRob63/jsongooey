package jsongooey.frontend.jsontree;

import jsongooey.backend.jsonmodel.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ValueVisitor implementation that visits any backend json value representation and returns with the swing TreeNode representation
 * current implementation returns a list of nodes
 * only the array and object methods return with multiple nodes, with the assumption that the caller takes care of the parent node
 * this is for ease of use, in practice the object method is called, and then it recursively parses the ObjectValue tree
 * and the object method ultimately provides all parent nodes
 */
public class GetNodesVisitor implements ValueVisitor<List<JsonTreeNode>> {
    @Override
    public List<JsonTreeNode> visit(BooleanValue boolValue) {
        return List.of(new JsonTreeNode(boolValue.toString(), ""));
    }

    @Override
    public List<JsonTreeNode> visit(NumberValue numberValue) {
        return List.of(new JsonTreeNode(numberValue.toString(), ""));
    }

    @Override
    public List<JsonTreeNode> visit(StringValue stringValue) {
        return List.of(new JsonTreeNode(stringValue.toString(), ""));
    }

    @Override
    public List<JsonTreeNode> visit(NullValue nullValue) {
        return List.of(new JsonTreeNode("null", ""));
    }

    @Override
    public List<JsonTreeNode> visit(ObjectValue objectValue) {

        List<JsonTreeNode> keyNodes = new ArrayList<>();
        for (var entry : objectValue.getMembers().entrySet())
        {
            String key = entry.getKey();
            Value modelValue = entry.getValue();
            String valueTypeString = modelValue.accept(new GetValueTypeVisitor());

            var keyRoot = new  JsonTreeNode("\"" + key + "\"", valueTypeString);

            for (var child : modelValue.accept(new GetNodesVisitor()))
            {
                keyRoot.add(child);
            }

            keyNodes.add(keyRoot);
        }

        return keyNodes;
    }

    @Override
    public List<JsonTreeNode> visit(ArrayValue arrayValue) {

        List <JsonTreeNode> elements = new ArrayList<>();
        var arrayModel = arrayValue.getArray();

        int i = 0;
        for (var value : arrayModel) {
            var elementRoot = new JsonTreeNode(String.valueOf(i), value.accept(new GetValueTypeVisitor()));

            for (var child : value.accept(new GetNodesVisitor())) {
                elementRoot.add(child);
            }

            elements.add(elementRoot);
            i++;
        }

        return elements;
    }
}
