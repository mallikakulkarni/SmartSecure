package edu.sjsu.smartsecure;

import edu.sjsu.smartsecure.decisionTree.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mallika on 3/26/16.
 */
public class NodeDictionary {
    private Map<String, Node> dictionary;
    private static NodeDictionary nodeDictionary;

    private NodeDictionary() {
        dictionary = new HashMap<String, Node>();
    }

    public static NodeDictionary getNodeDictionaryInstance() {
        if (nodeDictionary == null) {
            nodeDictionary = new NodeDictionary();
        }
        return nodeDictionary;
    }

    public Map<String, Node> getDictionary() {
        return dictionary;
    }

    public void addNode(Node node) {
        String nodeId = node.getNodeId();
        if (!dictionary.containsKey(nodeId)) {
            dictionary.put(nodeId, node);
        }
    }

    public Node getNode(String nodeId) {
        return dictionary.containsKey(nodeId) ? dictionary.get(nodeId) : null;
    }
}
