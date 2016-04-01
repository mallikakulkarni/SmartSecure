package edu.sjsu.smartsecure;

import javax.management.Query;
import javax.xml.crypto.Data;

/**
 * Created by mallika on 3/27/16.
 */
public class DecisionTree {
    private Node root;
    private NodeDictionary nodeDictionary;
    private DataAccess dataAccess;

    public DecisionTree() {
        root = new Node("0");
        nodeDictionary = NodeDictionary.getNodeDictionaryInstance();
        dataAccess = new DataAccess();
    }

    public Node getRoot() {
        return root;
    }

    public void addNode(String existingNodeId, String newNodeId, float probability) {
        Node node = new Node(newNodeId);
        node.setProbability(probability);
        Node parent = nodeDictionary.getNode(existingNodeId);
        parent.getForks().add(node);
    }

    public void classify(edu.sjsu.smartsecure.Query query) {
        String userId = query.getUserId();
        Node node;
        if (dataAccess.doesUserExist()) {
            node = root.getForks().get(0);

        }

    }
}
