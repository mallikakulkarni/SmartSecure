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

    public List<String> processTestData(JSONObject jObject) {
        JSONArray jsonArray = (JSONArray) jObject.get("realtimedata");
        Iterator<?> iterator = jsonArray.iterator();
        List<String> list = new ArrayList<String>();
        while (iterator.hasNext()) {
            JSONObject jsonObject = (JSONObject) iterator.next();
            decisionTreeLog.debug("Incoming JsonObject "+jsonObject);
            decisionTreeLog.debug("Starting DT Traversal");
            String result = traverseDecisionTreeForResult(root, jsonObject);
            list.add(result);
        }
        return list;
    }

    private String traverseDecisionTreeForResult(Node node, JSONObject jsonObject) {
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
        if (i == attributes.size()) {
            return "Safe";
        }
        Node child = node.getChildren().get(i);
        decisionTreeLog.debug("Going to child "+child.getNodeId());
        if (child.getResult() != null) {
            decisionTreeLog.debug("Getting Result "+child.getResult());
            return getResultMessage(child);
        }
        return traverseDecisionTreeForResult(child, jsonObject);
    }

    private String getResultMessage(Node node) {
        if (node.getResult() == -1) {
            return "Safe";
        }
        String result = "";
        switch (node.getResult()) {
            case 1:
                result = "Warning: Other users don't trust the " +node.getCorrespondingAttribute() + " app";
                break;
            case 2:
                result = "Warning: You are on an unsecure Wi-fi network";
                break;
            case 3:
                result = "Warning: Data Usage is very high";
                break;
            case 4:
                result = "Alert: You have used your phone at an unusual time.";
                break;
            case 5:
                result = "Alert: You have used your phone at an unusual time.";
                break;
            case 6:
                result = "Safe";
                break;
            case 7:
                result = "Warning: The usage statistics for all apps is very high";
                break;
            case 8:
                result = "Alert: You are in an unknown location.";
                break;
        }
        return result;
    }
}
