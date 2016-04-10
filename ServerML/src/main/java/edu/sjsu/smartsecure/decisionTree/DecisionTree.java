package edu.sjsu.smartsecure.decisionTree;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by mallika on 3/27/16.
 */
public class DecisionTree {
    private static DecisionTree decisionTree = null;
    private Node root;

    private DecisionTree() {
        root = null;
    }

    public static DecisionTree getDecisionTreeInstance() {
        if (decisionTree == null) {
            decisionTree = new DecisionTree();
        }
        return decisionTree;
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public boolean processTestData(JSONObject jsonObject) {
        return traverseDecisionTreeForResult(root, jsonObject);
    }

    private boolean traverseDecisionTreeForResult(Node node, JSONObject jsonObject) {
        String nodeId = node.getNodeId();
        String inputValue = jsonObject.get(nodeId).toString();
        List<String> attributes = node.getAttributes();
        int i;
        for (i = 0; i < attributes.size(); i++) {
            if (attributes.get(i).equals(inputValue)) {
                break;
            }
        }
        Node child = node.getChildren().get(i);
        return child.getResult() != null ? child.getResult() : traverseDecisionTreeForResult(child, jsonObject);
    }
}
