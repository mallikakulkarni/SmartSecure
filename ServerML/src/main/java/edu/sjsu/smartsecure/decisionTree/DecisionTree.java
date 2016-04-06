package edu.sjsu.smartsecure.decisionTree;

/**
 * Created by mallika on 3/27/16.
 */
public class DecisionTree {
    private static DecisionTree decisionTree = null;
    private Node root;

    private DecisionTree(Node root) {

        this.root = root;
    }

    public static DecisionTree getDecisionTreeInstance(Node root) {
        if (decisionTree == null) {
            decisionTree = new DecisionTree(root);
        }
        return decisionTree;
    }


}
