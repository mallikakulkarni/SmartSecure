package edu.sjsu.smartsecure.decisionTree;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
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
    static Logger decisionTreeLog = LoggerFactory.getLogger("decisionTree");

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

    public List<Integer> processTestData(JSONObject jObject) {
        JSONArray jsonArray = (JSONArray) jObject.get("realtimedata");
        Iterator<?> iterator = jsonArray.iterator();
        List<Integer> list = new ArrayList<Integer>();
        while (iterator.hasNext()) {
            JSONObject jsonObject = (JSONObject) iterator.next();
            decisionTreeLog.debug("Incoming JsonObject "+jsonObject);
            decisionTreeLog.debug("Starting DT Traversal");
            int result = traverseDecisionTreeForResult(root, jsonObject);
            list.add(result);
        }
        return list;
    }

    private int traverseDecisionTreeForResult(Node node, JSONObject jsonObject) {
        String nodeId = node.getNodeId();
        decisionTreeLog.debug("Node "+nodeId);
        String inputValue = jsonObject.get(nodeId).toString();
        decisionTreeLog.debug("Object Value "+inputValue);
        List<String> attributes = node.getAttributes();
        int i;
        for (i = 0; i < attributes.size(); i++) {
            System.out.println(attributes.get(i));
            if (attributes.get(i) == null || attributes.get(i).equals(inputValue)) {
                break;
            }
        }
        Node child = node.getChildren().get(i);
        decisionTreeLog.debug("Going to child "+child.getNodeId());
        if (child.getResult() != null) {
            decisionTreeLog.debug("Getting Result "+child.getResult());
        }
        return child.getResult() != null ? child.getResult() : traverseDecisionTreeForResult(child, jsonObject);
    }
}
