package edu.sjsu.smartsecure;

import com.sun.org.apache.xpath.internal.objects.XNodeSetForDOM;

/**
 * Created by mallika on 4/1/16.
 */
public class Model {

    public static void createModel() {
        DecisionTree decisionTree = new DecisionTree();
        NodeDictionary nodeDictionary = NodeDictionary.getNodeDictionaryInstance();
        Node root = decisionTree.getRoot();
        createPersonalAndGlobalUserPaths(root, nodeDictionary);
    }

    private static void createPersonalAndGlobalUserPaths(Node node, NodeDictionary nodeDictionary) {
        Node personal = new Node("rootforkpersonal", 100);
        Node global = new Node("rootforkglobal", 0);
        nodeDictionary.addNode(personal);
        nodeDictionary.addNode(global);
    }
}
