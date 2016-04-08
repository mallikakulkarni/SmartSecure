package edu.sjsu.smartsecure.decisionTree;

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
}
